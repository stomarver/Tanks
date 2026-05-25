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
- `TaknsApplet` extends `JApplet`, which is historical and no longer browser-usable.
- Keep for archival authenticity, but prefer desktop `main` for practical usage.

### 4) Java 8 is the safest baseline
- API usage is compatible with Java 8 style AWT/Swing era code.
- Using Java 8 avoids newer-toolchain regressions for legacy code.

## Delivered setup artifacts

1. `README.md` with IDEA and CLI quick-start.
2. `.run/Takns Desktop.run.xml` shared IntelliJ run configuration.
3. `.run/TitleBuilder Tool.run.xml` shared IntelliJ run configuration.
4. `scripts/compile-java8.sh` deterministic compile script.
5. `.gitignore` tuned for IntelliJ and local build outputs.

## Suggested next steps (optional)

- Add Gradle with Java Toolchains pinned to 8.
- Move generated/runtime assets to a dedicated `assets/` directory (if introduced).
- Add a CI compile check (`javac` or Gradle `build`) to guard regressions.
