# Screen Lock

A simple Android app to lock your screen with a single tap, designed to work on all Android devices including OnePlus.

## Features

- **Two Screen Lock Methods**: Choose between Accessibility Service or Device Admin
- **OnePlus Compatible**: Device Admin method works reliably on OnePlus devices
- **No Root Required**: Works without rooting your device
- **Privacy Focused**: No data collection or sharing
- **Instant Lock**: Lock your screen immediately from the launcher icon

## How It Works

The app provides two methods to lock your screen:

### Method 1: Accessibility Service (Recommended)
- **Best for**: Most devices where you want to unlock with biometrics (fingerprint/face)
- **How to enable**: 
  1. Open the app
  2. Tap "Enable Accessibility Service"
  3. Find "Screen Lock" in the accessibility settings
  4. Turn it on
- **Advantages**: 
  - ✓ Allows biometric unlock (fingerprint/face)
  - ✓ Quick and convenient unlock experience
- **Limitations**:
  - ✗ May not work on OnePlus devices (accessibility restrictions)

### Method 2: Device Admin (Fallback for OnePlus)
- **Best for**: OnePlus devices and any device where accessibility services are restricted
- **How to enable**:
  1. Open the app
  2. Tap "Enable Device Admin"
  3. Grant the device admin permission
- **Advantages**:
  - ✓ Works reliably on all devices including OnePlus
  - ✓ No accessibility restrictions
- **Limitations**:
  - ✗ Requires PIN/Password to unlock (biometric unlock not available immediately after locking)

## Which Method Should I Use?

**Try Accessibility Service first** if you want to unlock with biometrics. If it doesn't work on your device (e.g., OnePlus), enable Device Admin as a fallback.

If both methods are enabled, the app will prefer Accessibility Service to allow biometric unlock.

## Usage

1. Install the app
2. Enable Accessibility Service (preferred) or Device Admin (if accessibility doesn't work)
3. Tap the Screen Lock icon in your launcher to instantly lock your screen

## OnePlus Users

OnePlus devices restrict accessibility services for third-party apps. You have two options:

### Option 1: Try Accessibility Service First
Try enabling the Accessibility Service. If OnePlus allows it, you'll be able to unlock with biometrics.

### Option 2: Use Device Admin (if accessibility doesn't work)
1. Enable Device Admin in the app
2. The Device Admin method works reliably on OnePlus devices
3. **Note**: You'll need to unlock with PIN/Password (not biometric) after locking
4. You can revoke the Device Admin permission anytime from Android Settings → Security → Device admin apps

## Privacy

- The app only requests permissions necessary to lock your screen
- No data is collected, stored, or shared
- Open source - you can review the code yourself

## Requirements

- Android 11 (API 30) or higher
- No root access required

## Permissions

- **Accessibility Service**: To lock the screen (Method 1 - allows biometric unlock)
- **Device Admin**: Alternative method to lock the screen (Method 2 - works on OnePlus but requires PIN/Password unlock)

Both permissions can be revoked at any time through your device settings.

## Troubleshooting

**Q: The app icon doesn't immediately lock my screen**
- Make sure you've enabled at least one lock method (Accessibility Service or Device Admin)

**Q: Accessibility Service doesn't work on my OnePlus device**
- This is a known limitation on OnePlus devices. Use the Device Admin method instead.

**Q: I can't unlock with fingerprint after using Device Admin to lock**
- This is a limitation of the Device Admin API in Android. If you want biometric unlock, use the Accessibility Service method instead (if your device allows it).

**Q: How do I remove Device Admin permission?**
- Go to Settings → Security → Device admin apps → Screen Lock → Deactivate

**Q: Can I use both methods at the same time?**
- Yes! If both are enabled, the app will prefer Accessibility Service to allow biometric unlock.

## License

[Add your license here]
