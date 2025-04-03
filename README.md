# Chess Evolved

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a Kotlin project template that includes Kotlin application launchers and [KTX](https://libktx.github.io/) utilities.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

## Setting up Supabase for production

Add a .env file to the project and add `SUPABASE_ADMIN_KEY={service_role_key}` and `SUPABASE_URL={project_url}` to this file.
> You should replace {service_role_key} and {project_url} with the key and url you find under Project Settings->Data API on supabase.

That's it!

## Creating a local Supabase database

When working on the project, you should be doing development within a dev-database, making sure not to use the prod database. Here is the how-to:

### Get it up and running

1. Install [Supabase CLI](https://supabase.com/docs/guides/local-development/cli/getting-started). This guide tells you to install a docker compatible API as well. [Docker Desktop](https://docs.docker.com/desktop/) works well for this purpose.
2. If you're on windows, open up Docker Desktop, go to Settings->General and turn on `Add the *.docker.internal names to the host's /etc/hosts file (Requires password)`.
3. Keep Docker Desktop running in the background.
4. Open a terminal in the root directory of the project repository and run `supabase start`.
5. Add a .env file to the root of the repository and add `SUPABASE_ADMIN_KEY={service_role_key}` and `SUPABASE_URL={project_url}` to this file.

> Look at the output from your "supabase start" command. You should replace {service_role_key} with the string after "service_role key:" and {project_url} with the string after "API URL:"

6. While your database is running, you can access it's dashboard at <http://localhost:54323>.
7. You can now run the project, and it should connect to your local database.
8. When you're done developing, you can run `supabase stop` to stop your database.

### Extend functionality

> If you want to alter supabase-files in the repo, you need to run `supabase stop --no-backup` while the database is running. This ensures that next time you run `supabase start`, it'll rebuild the database using the repo's files.

If you make changes on the remote supabase database, you can pull those changes by following these steps:

1. You first need to run `supabase login`.
2. Afterwards you need to link the remote database to this project by running `supabase link --project-ref tsmubattgglbqaarktnw`. You will be prompted for a password. The password is saved on The Progarkening google-drive shared with the whole group.
3. After linking, you should now be able to pull the schema from supabase by using the command `supabase db pull`. It might tell you to run a `supabase migration repair`-command, and you should do that if it tells you. If you had to run the migration repair command, run `supabase db pull` one last time.
4. That should do it!

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

### Format and linting

The tool we use for formatting and linting is [ktlint](https://ktlint.github.io/). It does both. To run it, use the following command:

```bash
./gradlew ktlintCheck
```

This will check the code for formatting and lint issues. To automatically fix them, run:

```bash
./gradlew ktlintFormat
```

This will only fix some of the issues because ktlint is not perfect. The rest of the issues will need to be fixed manually.

#### Automatic formatting

For Android Studio users, follow these steps to integrate ktlint:

1. Go to Android Studio → Settings/Preferences → Plugins
2. Search for "Ktlint" by "jansorg"
3. Install the plugin and restart Android Studio
4. Go to Android Studio → Settings/Preferences → Tools → Ktlint
5. Enable "ktlint"

For VSCode or Neovim users, you know what you are doing.

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
