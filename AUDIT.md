# Takns audit (2026-05-25)

## Scope

- Source layout and entry points.
- Java runtime compatibility for local desktop run.
- Friction points for modern IDE setup.

## Findings

### 1) Project structure is classic source-only Java
- Sources are under `src/com/mojang/takns/...` with no existing build wrapper.
- This is suitable for plain `javac` builds and easy migration to Gradle later.

### 2) Desktop entry point exists and is valid
- `com.mojang.takns.Takns` defines `public static void main(String[] args)`.
- The main method creates a `JFrame`, attaches the game canvas, calls `init()` and `start()`.

### 3) Applet entry point is legacy-only
- `TaknsApplet` extends `JApplet`.
- On modern JDKs this API is removed, so full-source compile can fail.
- Practical default: compile/run desktop target; keep applet path only for archival compatibility.

### 4) Java 8 compatibility strategy
- Script now prefers `javac --release 8` when available.
- This removes `-source/-target` warning noise and provides cleaner cross-version behavior.
- Script includes optional `--with-applet` mode for strict legacy JDK 8 environments.

## Delivered setup artifacts

1. `README.md` with IDEA and CLI quick-start.
2. `.run/Takns Desktop.run.xml` shared IntelliJ run configuration.
3. `.run/TitleBuilder Tool.run.xml` shared IntelliJ run configuration.
4. `scripts/compile-java8.sh` deterministic compile script.
5. `.gitignore` tuned for IntelliJ and local build outputs.

## Suggested next steps (optional)

- Add Gradle with Java Toolchains pinned to 8.
- Add a dedicated legacy profile/task that can include applet source explicitly.
- Add a CI compile check for the desktop profile to guard regressions.
