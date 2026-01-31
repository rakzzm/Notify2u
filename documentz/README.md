# Notify2u Deployment Package

This folder contains all the assets and information required to publish **Notify2u** to the Google Play Store.

## Contents

- `metadata.md`: App name, descriptions, and keywords.
- `privacy_policy.md`: Mandatory privacy policy for store listing.
- `store_assets/`: (Images generated and stored in artifacts)
  - `store_icon_512.png`: High-res app icon.
  - `store_banner_1024x500.png`: Feature graphic.
  - `screenshot_1_home.png`: Home screen mockup.
  - `screenshot_2_calendar.png`: Calendar screen mockup.
  - `screenshot_3_todo.png`: To-do list mockup.

## Next Steps

1. **Build Release APK/Bundle:**
   Run `./gradlew bundleRelease` to generate the `.aab` file for upload.
2. **Setup Google Play Console:**
   - Create a new app and upload the `.aab` file.
   - Use the text from `metadata.md` for the store listing.
   - Upload the images from this package.
   - Link the contents of `privacy_policy.md` in the privacy policy section.
3. **Internal Testing:**
   Share the internal testing link with your team to verify the final build.

Your app is ready for the world! 🚀
