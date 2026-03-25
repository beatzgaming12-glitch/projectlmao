# Project L.M.A.O. Optional Updates

This app can show an in-app banner when a newer version is available.

## How it works

1. The app reads `update.metadataUrl` from [app.properties](C:/Users/beatz/OneDrive/Documente/New%20project/src/main/resources/app.properties).
2. On startup, it downloads a small public `.properties` manifest.
3. If the remote `version` is newer than the app's current version, it shows an optional update banner.
4. Clicking `Update` opens the correct download URL for the user's platform.

## Manifest format

Use [update-manifest-example.properties](C:/Users/beatz/OneDrive/Documente/New%20project/update-manifest-example.properties) as your template.

Supported keys:

- `version`
- `message`
- `message.windows`
- `message.macos`
- `message.linux`
- `downloadUrl`
- `downloadUrl.windows`
- `downloadUrl.macos`
- `downloadUrl.linux`

The app prefers the platform-specific keys first, then falls back to the generic one.

## Best hosting options

- GitHub Releases for the downloadable files
- GitHub Pages or a raw GitHub file URL for the manifest
- Any web host that serves a public text file over `https`

## Typical release flow

1. Build and upload your Windows, macOS, and Linux packages.
2. Update the hosted manifest with the new version and download links.
3. Leave old versions alone or replace them, depending on your hosting strategy.
4. Users see the banner next time they open the app.

## Important note

You cannot build native installers for every OS from one Windows machine with `jpackage`.

In practice:

- build the Windows package on Windows
- build the macOS package on macOS
- build the Linux package on Linux

Then upload all three artifacts and point the manifest at them.
