import "./globals.css";
import AuthProvider from "./components/AuthProvider";

export const metadata = {
  title: "Currency Tracker",
  description: "Real-time currency tracking dashboard",
};

export default function RootLayout({ children }) {
  return (
    <html lang="tr">
      <head>
        <link rel="manifest" href="/manifest.json" />
        <meta name="theme-color" content="#1f2937" />
        {
    }
        <meta name="apple-mobile-web-app-capable" content="yes" />
        <meta name="mobile-web-app-capable" content="yes" />
        {
  }
        <meta name="apple-mobile-web-app-status-bar-style" content="default" />
        <meta name="apple-mobile-web-app-title" content="KurTakip" />
        <link rel="apple-touch-icon" href="/icon-192x192.png" />
      </head>
      <body>
        <AuthProvider>{children}</AuthProvider>
      </body>
    </html>
  );
}

