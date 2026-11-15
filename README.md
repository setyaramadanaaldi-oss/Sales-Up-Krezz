# KrezzWafa Sales — Android

Android WebView app for the live KrezzWafa Sales Dashboard.

Dashboard: https://script.google.com/macros/s/AKfycbzqwtmEvZx4Eeh6s_XWRbkgVwjKuGxPmKrOjBOYOPG_sQHVe3uWtBaJJhpGMyNssl6/exec

## Build and download
1. Open the repository **Actions** tab.
2. Choose **Build KrezzWafa APK** and select **Run workflow**.
3. Wait for the workflow to finish successfully.
4. Download `KrezzWafa-Sales.apk` from the run's Artifacts section, or open **Releases** for the generated pre-release.

The workflow fetches the supplied logo, builds an Android WebView APK, and uploads the installable file. The app requires internet because dashboard data remains in Google Apps Script / Google Sheets. External URLs are handed to Android apps or the browser.

This initial APK is debug-signed for testing. Before broad distribution, create a private release signing key and store it in GitHub Actions secrets.
