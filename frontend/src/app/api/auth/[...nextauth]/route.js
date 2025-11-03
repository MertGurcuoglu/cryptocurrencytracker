import NextAuth from "next-auth";
import GoogleProvider from "next-auth/providers/google";

export const authOptions = {
    providers: [
        GoogleProvider({
            clientId: process.env.GOOGLE_CLIENT_ID,
            clientSecret: process.env.GOOGLE_CLIENT_SECRET,
        }),
    ],
    callbacks: {
        async jwt({ token, account }) {
            console.log("\n--- JWT CALLBACK BAŞLADI ---");

            if (account) {
                console.log("İlk giriş tespit edildi, Google account bilgisi mevcut.");
                try {
                    console.log("API Gateway'e istek gönderiliyor: POST http://localhost:8080/api/auth/google");
                    
                    const res = await fetch("http://localhost:8080/api/auth/google", {
                        method: "POST",
                        headers: { "Content-Type": "application/json" },
                        body: JSON.stringify({ token: account.id_token }),
                    });

                    console.log("API Gateway'den cevap alındı. Status Kodu:", res.status);

                    if (res.ok) {
                        const data = await res.json();
                        console.log("Cevap başarılı (200 OK). Gelen JWT:", data.jwt);
                        token.backendJwt = data.jwt;
                        console.log("backendJwt, NextAuth token'ına eklendi.");
                    } else {
                        const errorBody = await res.text();
                        console.error("API Gateway'den başarısız cevap alındı. Cevap içeriği:", errorBody);
                        token.error = "BackendTokenError";
                    }
                } catch (error) {
                    console.error("API Gateway'e fetch isteği sırasında KRİTİK HATA oluştu:", error);
                    token.error = "BackendFetchError";
                }
            } else {
                console.log("Bu bir ilk giriş değil, mevcut token kullanılıyor.");
            }
            
            console.log("--- JWT CALLBACK BİTTİ ---");
            return token;
        },
        async session({ session, token }) {
            session.backendJwt = token.backendJwt;
            session.error = token.error;
            return session;
        },
    },
};

const handler = NextAuth(authOptions);
export { handler as GET, handler as POST };