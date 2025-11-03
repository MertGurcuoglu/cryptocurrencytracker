"use client";

import { SessionProvider } from "next-auth/react";

// Bu bileşen, SessionProvider'ı bir Client Component içinde sarmalayarak
// App Router ile uyumlu hale getirir.
export default function AuthProvider({ children }) {
  return <SessionProvider>{children}</SessionProvider>;
}
