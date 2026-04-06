# TypeSmart (Android AI Keyboard)

TypeSmart is a custom Android keyboard (IME) built in Kotlin. It lets users rewrite text in-place with AI actions like Rewrite, Professional, Casual, Translate to English, Funny, and Flirty.

## Requirements

- Android Studio Hedgehog or newer (Ladybug works too)
- Android SDK installed
- JDK 17 (Android Studio usually bundles this)
- Android device or emulator running Android 7.0+ (API 24+)

## Project Info

- Min SDK: 24
- Target SDK: 34
- Language: Kotlin
- UI: XML layouts (no Compose)
- Networking: OkHttp
- JSON: Gson

## Open the project in Android Studio

1. Open Android Studio.
2. Click **Open**.
3. Select this folder:
   - `/Users/sunil.sharma/Documents/TypeSmart`
4. Wait for indexing to finish.

## Sync Gradle

1. Android Studio should prompt Gradle sync automatically. Click **Sync Now**.
2. If it does not:
   - Click **File > Sync Project with Gradle Files**.
3. If prompted for SDK components, click **Install** and wait.

## Run the app

1. Select run configuration **app** (top toolbar).
2. Choose a device:
   - **Physical device**: enable USB debugging and connect.
   - **Emulator**: open **Device Manager**, create/start an AVD (API 24+).
3. Click **Run** (green play button).
4. The `TypeSmart` onboarding screen should open.

## Enable and select TypeSmart keyboard (IME flow)

After launching the app:

1. Tap **Open Keyboard Settings**.
2. In Android keyboard settings, enable **TypeSmart Keyboard**.
3. Go back to the app.
4. Tap **Choose Input Method**.
5. Select **TypeSmart Keyboard**.
6. Step 3 appears with a test text field.

## Test AI actions end-to-end

1. Open any text field (or the app's test field).
2. Type a sentence (or select existing text).
3. Tap `✨ AI` on the keyboard.
4. Choose an action (Rewrite, Professional, etc.).
5. Wait for response:
   - **Replace** inserts AI text into the input field.
   - **Copy** copies AI result to clipboard.

## API endpoint setup

Current placeholder endpoint:

- `https://api.myapp.com/rewrite`

Update it in:

- `app/src/main/java/com/typesmart/keyboard/ApiClient.kt`

Expected request JSON:

```json
{ "text": "user text here", "action": "professional" }
```

Expected response JSON:

```json
{ "result": "improved text here" }
```

## Common issues

- **Gradle sync fails due to JDK**
  - Set Gradle JDK to 17:
  - **Android Studio > Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK**
- **Keyboard not visible in picker**
  - Confirm app is installed.
  - Confirm TypeSmart is enabled in system keyboard settings.
- **AI call fails**
  - Verify internet access on emulator/device.
  - Check endpoint URL and backend response format.

## Useful commands (optional, terminal)

From project root:

```bash
./gradlew tasks
./gradlew assembleDebug
```

If `./gradlew` fails in terminal, ensure Java is installed and `JAVA_HOME` is set.
