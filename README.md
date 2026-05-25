# Takns (classic Mojang prototype)

This repository contains source code for `com.mojang.takns` (Java/AWT/Swing era project).

## Quick start (IntelliJ IDEA)

1. Install and select **JDK 8** for the project.
2. Open the repository as a project folder.
3. Mark `src` as a **Sources Root**.
4. Use the shared run configurations in `.run/`:
   - `Takns Desktop` → starts the playable desktop app.
   - `TitleBuilder Tool` → utility generator from `intro/TitleBuilder`.

## CLI compile/run (without Gradle/Maven)

Desktop-oriented compile (default; excludes legacy applet file):

```bash
./scripts/compile-java8.sh
java -cp out com.mojang.takns.Takns
```

Compile including `TaknsApplet` (requires real JDK 8 toolchain):

```bash
./scripts/compile-java8.sh --with-applet
```

## Why applet is excluded by default

`TaknsApplet` uses `javax.swing.JApplet`, which was removed from modern JDK distributions.
To keep the project runnable on current systems, default CLI build targets desktop entry point only.

> Note: this project is intentionally kept build-tool agnostic here.
> You can add Gradle/Maven yourself later without changing source layout.
