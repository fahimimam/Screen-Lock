# Screen Lock

A simple Android app to lock your screen with a single tap, designed to work on all Android devices including OnePlus.

## Features

- **Two Screen Lock Methods**: Choose between Device Admin or Accessibility Service
- **OnePlus Compatible**: Device Admin method works reliably on OnePlus devices
- **No Root Required**: Works without rooting your device
- **Privacy Focused**: No data collection or sharing
- **Instant Lock**: Lock your screen immediately from the launcher icon

## How It Works

The app provides two methods to lock your screen:

### Method 1: Device Admin (Recommended for OnePlus)
- **Best for**: OnePlus devices and any device where accessibility services are restricted
- **How to enable**: 
  1. Open the app
  2. Tap "Enable Device Admin"
  3. Grant the device admin permission
- **Advantage**: Works reliably on all devices without accessibility restrictions

### Method 2: Accessibility Service (Alternative)
- **Best for**: Devices that allow accessibility services
- **How to enable**:
  1. Open the app
  2. Tap "Enable Accessibility Service"
  3. Find "Screen Lock" in the accessibility settings
  4. Turn it on
- **Note**: Some manufacturers (like OnePlus) restrict accessibility services for security reasons

## Usage

1. Install the app
2. Enable at least one lock method (Device Admin is recommended for OnePlus users)
3. Tap the Screen Lock icon in your launcher to instantly lock your screen

## OnePlus Users

If you're using a OnePlus device (like OnePlus 8T) and having trouble with the accessibility method:

1. **Use Device Admin instead**: This is the recommended method for OnePlus devices
2. The Device Admin method simulates a power button press to lock the screen
3. It works without requiring accessibility permissions
4. You can revoke the Device Admin permission anytime from Android Settings → Security → Device admin apps

## Privacy

- The app only requests permissions necessary to lock your screen
- No data is collected, stored, or shared
- Open source - you can review the code yourself

## Requirements

- Android 11 (API 30) or higher
- No root access required

## Permissions

- **Device Admin**: To lock the screen (Method 1)
- **Accessibility Service**: Alternative method to lock the screen (Method 2)

Both permissions can be revoked at any time through your device settings.

## Troubleshooting

**Q: The app icon doesn't immediately lock my screen**
- Make sure you've enabled at least one lock method (Device Admin or Accessibility Service)

**Q: Accessibility Service doesn't work on my OnePlus device**
- This is a known limitation on OnePlus devices. Use the Device Admin method instead.

**Q: How do I remove Device Admin permission?**
- Go to Settings → Security → Device admin apps → Screen Lock → Deactivate

**Q: Can I use both methods at the same time?**
- Yes! The app will prefer Device Admin if both are enabled.

## License

[Add your license here]
