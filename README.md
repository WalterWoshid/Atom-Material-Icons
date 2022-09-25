# Atom Material Icons Plugin for JetBrains

<!--suppress HtmlDeprecatedAttribute -->
<h1 align="center">
  <br>
    <img src="src/main/resources/META-INF/pluginIcon.svg" alt="logo" width="320" height="320">
  <br><br>
  Atom Material Icons
  <br>
  <br>
</h1>


This plugin is a port of the [Atom File Icons](https://github.com/file-icons/atom) for Atom and the icons of
the [Material Theme UI](https://github.com/mallowigi/material-theme-issues) plugin.

## Build

### IntelliJ IDEA

- Open the project in IntelliJ IDEA
- Pull the submodules
  - `git submodule update --init --recursive`
- Settings > Build, Execution, Deployment > Build Tools > Gradle
  - Select your Project
  - Build and run using: IntelliJ IDEA
  - Run tests using: IntelliJ IDEA
  - Gradle JVM: JetBrains Runtime version 17.x
- Build the project
  - Build > Build Project
- Add new Run/Debug Configuration
  - Type: Gradle
  - Name: `Build Plugin`
  - Run: `buildPlugin`
  - Run the configuration

## Chrome Extension

<https://chrome.google.com/webstore/detail/atom-file-icons-web/pljfkbaipkidhmaljaaakibigbcmmpnc>

## Firefox Extension

<https://addons.mozilla.org/en-US/firefox/addon/atom-file-icons-web/>

## Features

- Replaces **file icons** with their relevant logo icons
    - According to their extension (Java, PHP, Ruby...)
    - According to the framework (Android, NPM, RSpec...)
    - According to the program used with (Babel, Docker, CircleCI...)
- Replaces **directories**:
    - With a common pattern: src, main, app, img, docs...
    - With a specific pattern: node_modules, .vscode, .git...
- Replaces the **PSI (Program Structure Interface)** icons:
    - Classes, Interfaces, Enums, Abstract...
    - Methods, Lambdas, Inherits, Overrides...
- Replaces the **UI Icons**:
    - Toolbar icons (actions)
    - Gutter icons (debugger)
    - Node icons (folders)...
- **Monochrome filter**: Set a monochrome filter to the whole UI
    - Ability to select the color
- **Arrows Style**: Customize the look of the arrows in Tree components
    - Material: Chevron-like arrows
    - Darcula: Regular triangle arrows
    - Plus-Minus: Plus and Minus signs
    - Arrows: Simple Arrows
    - None: No arrows
- **Hollow Folders**: Add custom decorations on folders contianing opened files
- **Custom Icon Size**: Assign a custom icon size (between 12 and 24)
- **Accent Color**: Change the color of specific icons
    - Closing tab button
    - Highlighted arrows
    - Loading indicator...
- **Customizability**: Custom Settings
    - Settings Pages
    - Toolbar Action Buttons
- **Custom File and Directory associations** Customize your own associations
    - File associations by Regex
    - Folder associations
    - Preview default icons

## File Icons

![File Icons](https://raw.githubusercontent.com/mallowigi/iconGenerator/master/assets/files.png)

## Folder Icons

![Folder Icons](https://raw.githubusercontent.com/mallowigi/iconGenerator/master/assets/folders.png)
