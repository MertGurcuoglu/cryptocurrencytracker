"use client";

import { useSession } from "next-auth/react";
import { useRouter } from "next/navigation";
import { useEffect } from "react";

export default function HomePage() {
  const { status } = useSession();
  const router = useRouter();

  useEffect(() => {
    if (status === "authenticated") {
      router.replace("/dashboard");
    }
    
    if (status === "unauthenticated") {
      router.replace("/login");
    }
  }, [status, router]);

  return (
      <div className="flex items-center justify-center h-screen bg-gray-100">
          <p className="text-lg text-gray-600">Yönlendiriliyor...</p>
      </div>
  );
}

