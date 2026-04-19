# Project L.M.A.O.

Desktop app bundle for `Project L.M.A.O.`.

## Windows Download

Download the latest packaged app zip here:

[ProjectLMAO-windows.zip](https://github.com/beatzgaming12-glitch/projectlmao/raw/main/releases/ProjectLMAO-windows.zip)

Repo page:

[https://github.com/beatzgaming12-glitch/projectlmao](https://github.com/beatzgaming12-glitch/projectlmao)

## Windows Run

1. Download `ProjectLMAO-windows.zip`.
2. Extract the zip to a normal folder.
3. Open the extracted `ProjectLMAO` folder.
4. Run `ProjectLMAO.exe`.

## Linux Build

There is no prebuilt Linux binary uploaded from this Windows machine.

Linux users can build the AppImage from source:

```bash
git clone https://github.com/beatzgaming12-glitch/projectlmao
cd projectlmao
./gradlew prepareLinuxAppImage
```

If `appimagetool` is installed on Linux, build the final AppImage with:

```bash
./gradlew buildLinuxAppImage
```

Expected output:

```bash
build/distributions/ProjectLMAO-1.1.2.AppImage
```

The Linux icon source used for packaging is:

`src/main/packaging/app-icon.png`

## Important

- Do not run it from inside the zip.
- If Windows shows an older icon, delete any old extracted copy first and extract this one to a fresh folder.
- If the app was pinned before, unpin the old one and pin the newly launched one again.

## Build From Source

From the project folder:

```powershell
.\gradlew.bat run
```

To build the packaged app image:

```powershell
.\gradlew.bat jpackageImage
```
