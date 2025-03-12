# Chess Evolved

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a Kotlin project template that includes Kotlin application launchers and [KTX](https://libktx.github.io/) utilities.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.

### Running

To run the project use the following command:

```bash
./gradlew lwjgl3:run
```

Then to create the build .jar file use the following command:

```bash
./gradlew lwjgl3:jar
```

The `.jar` file will be stored in the `lwjgl3/build/libs` directory.

Run the following command to clean the build directory:

```bash
./gradlew clean
```

#### Emulator

Deploy to emulator:

```bash
./gradlew android:installDebug
```

Run the application:

```bash
./gradlew android:run
```

Create APK:

```bash
./gradlew android:assembleRelease
```

### Debug and Testing

This is not setup yet! Remove this when setup is complete and add GitHub action.

Run unit tests:

```bash
./gradlew test
```

Run unit tests with coverage:

```bash
./gradlew test jacocoTestReport
```

### Dependencies

- [KTX](https://libktx.github.io/) - Kotlin extensions for LibGDX
- [Ashley](https://github.com/libgdx/ashley) - Entity Component System
