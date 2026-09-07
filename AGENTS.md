# Flight Club — notes for agents

A cross-country gliding simulator written by Dan Burton in 2001 as a Java applet.
The simulation is a hand-rolled 3D engine: no OpenGL, no scene graph library, no
external dependencies at all. It draws filled polygons and lines, and that is the
whole of its contract with the outside world.

`README.md` explains the game itself — thermals, ridge lift, the 100 km race.
This file explains the code.

## Build and run

The build needs **JDK 17**. Gradle 8.11.1 does not support Java 24+, and the
source is compiled at `sourceCompatibility 1.7`, which javac drops in JDK 20.

```bash
JAVA_HOME=/opt/homebrew/opt/openjdk@17 ./gradlew build          # everything
JAVA_HOME=/opt/homebrew/opt/openjdk@17 ./gradlew :javase:run    # desktop app
JAVA_HOME=/opt/homebrew/opt/openjdk@17 ./gradlew :web:webApp    # browser build
```

`:web:webApp` assembles `web/build/webApp/` — `index.html`, `flightclub.js`, and
the vario sounds. Serve that directory over HTTP and open it; `file://` will not
work.

```bash
cd web/build/webApp && python3 -m http.server 8765
```

There are no tests.

## Modules

```
core     the simulation. pure Java, no AWT, no browser
 ├─ awt     desktop/applet front end
 │   ├─ javase   Frame wrapper, main class org.flightclub.XCGameFrame
 │   └─ applet   Applet wrapper (see "dead weight" below)
 └─ web     TeaVM front end, compiles core to JavaScript
```

`core` must stay free of `java.awt` and browser types. That is the property that
makes the web port possible, and it is easy to break by adding one import.

```bash
grep -rn --include='*.java' 'java\.awt\|org\.teavm' core/src   # should find nothing
```

## The platform seam

Everything platform-specific goes through four small abstractions. A new front
end implements these and nothing else.

| Abstraction | What a front end supplies |
|---|---|
| `compat.Graphics` | 6 drawing primitives — `setColor`, `setFont`, `drawLine`, `drawString`, `fillCircle`, `fillPolygon` |
| `Interface` | canvas width and height, and `play(String)` for the vario beeps |
| `Clock.Driver` | the frame loop — calls `clock.tick()` once per frame |
| `compat.KeyEvent` | key codes; the front end converts its own event type |

Implementations:

| | desktop (`awt`) | browser (`web`) |
|---|---|---|
| graphics | `compat.AwtGraphics` | `compat.CanvasGraphics` |
| environment | `FrameInterface` / `AppletInterface` | `WebInterface` |
| frame loop | `ThreadClockDriver` (thread + sleep) | `AnimationFrameClockDriver` (`requestAnimationFrame`) |
| keys | `AwtKeys` | `WebKeys` |
| canvas | `ModelCanvas` | `WebCanvas` |

`compat.KeyEvent`'s constants deliberately mirror `java.awt.event.KeyEvent`, so
`AwtKeys.convert` is only a change of type. `WebKeys` maps DOM
`KeyboardEvent.code` strings (`"KeyA"`, `"ArrowLeft"`, `"Digit3"`) onto them.

Roughly 4,000 of the 5,400 lines — `Landscape`, `Cloud`, `Hill`, `Glider`,
`CameraMan`, `Tools3d`, `MovementManager`, `Object3d`, `FlyingDot` — never touch
any of this and need no changes when adding a front end.

## Wiring a front end

Order matters. `XCGame`'s constructor registers itself as clock observer 0, and
`Clock.tick()` ticks only observer 0 while paused — that is what lets you change
point of view and unpause. The canvas must therefore be constructed *after* the
`XCGame`.

```java
XCGame app = new XCGame();          // observer 0
WebCanvas panel = new WebCanvas(app, canvas);   // observer 1
panel.init();
app.init(new WebInterface(canvas));
app.clock.setDriver(new AnimationFrameClockDriver());
app.start();
```

Forgetting `setDriver` gives you a window that renders one frame and then sits
still — the clock has no loop to tick it.

## The web port

`web` uses the [TeaVM](https://teavm.org) Gradle plugin to compile `core` plus
the `web` front end into a single JavaScript file. TeaVM reads bytecode, so
`core` keeps compiling at Java 7 for the desktop build while `web` itself is
compiled at 17 (TeaVM's own classlib requires it).

- Entry point: `org.flightclub.XCGameWeb`, exported as a global `main()`.
- Output: UMD module, `web/build/generated/teavm/js/flightclub.js`.
- Size: ~584 KB with source maps on, ~253 KB obfuscated, ~81 KB gzipped.
- `obfuscated`/`sourceMap` are set in `web/build.gradle`; the checked-in settings
  favour debugging, so flip them for a real deploy.

Canvas differences worth remembering when touching `CanvasGraphics`:

- AWT's `fillOval` takes the bounding box, canvas `arc` takes a centre and
  radius.
- Lines are offset by half a pixel so a 1px line lands on the pixel grid instead
  of smearing across two rows.
- AWT logical font names (`SansSerif`, `Monospaced`) mean nothing to a browser
  and are mapped to CSS families.

Known gaps in the web build: no touch controls, the canvas is fixed at 640×490
rather than resizing, and browsers will not play the vario until the user has
interacted with the page.

## Traps

**Line endings are mixed.** 31 of the 44 Java files outside `web` are CRLF, the
rest LF, and there is no `.gitattributes`. Read and write files in a way that preserves what
is already there — a whole-file rewrite that flips the endings turns a two-line
change into a 300-line diff.

**`applet` is dead weight.** The Applet API was deprecated for removal in JDK 17
and removed in JDK 24 (JEP 504); browsers dropped plugin support years before
that. It still compiles on JDK 17 and is still wired
up, but nothing can run it. The `web` module is its replacement.

**Braces.** Existing code has plenty of brace-less single-line `if` statements.
New code should always use braces.

**No dependency injection, no framework.** `XCGame` holds public and
package-private fields that everything reaches into directly (`app.clock`,
`app.cameraMan`, `app.eventManager`). Front-end classes live in package
`org.flightclub` precisely so they can. Keep new front-end classes in that
package.

## Controls

Handled in `XCGame.keyPressed` (game and camera) and `GliderUser.keyPressed`
(flying).

| Key | Action |
|---|---|
| `Y` | start play — take control of the glider |
| `A` / `D`, arrows | turn left / right |
| `Space` | slow to minimum sink and work the lift |
| `W` / `S` | speed bar on / off |
| `P` | pause |
| `Q` | fast forward |
| `H` / `G` | cloudbase high / low |
| `1`–`4` | camera: glider, gaggle, plan view, distant view |
| `K` `L` `N` `M` | pan the camera |
| mouse drag | rotate the camera — works while paused, for a bullet-time effect |
