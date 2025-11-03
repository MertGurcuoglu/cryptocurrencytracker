"use client";

import { useState } from "react";
import { signIn } from "next-auth/react";

// --- Icon and Logo Components ---

const CoinMarketMertLogo = (props) => (
  <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}>
    <title>CoinMarketMert Logo</title>
    <circle cx="20" cy="20" r="18" stroke="currentColor" strokeWidth="2.5" />
    <path d="M10 23L15.5 17L22 24L30 16" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" />
  </svg>
);

const GoogleIcon = (props) => (
  <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 48 48" {...props}>
    <title>Google Icon</title>
    {

   }
    <path fill="#FFC107" d="M43.611 20.083H42V20H24v8h11.303c-1.649 4.657-6.08 8-11.303 8c-6.627 0-12-5.373-12-12s5.373-12 12-12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4C12.955 4 4 12.955 4 24s8.955 20 20 20s20-8.955 20-20c0-1.341-.138-2.65-.389-3.917z"/>
    <path fill="#FF3D00" d="M6.306 14.691l6.571 4.819C14.655 15.108 18.961 12 24 12c3.059 0 5.842 1.154 7.961 3.039l5.657-5.657C34.046 6.053 29.268 4 24 4C16.318 4 9.656 8.337 6.306 14.691z"/>
    <path fill="#4CAF50" d="M24 44c5.166 0 9.86-1.977 13.409-5.192l-6.19-5.238C29.211 35.091 26.715 36 24 36c-5.222 0-9.612-3.24-11.28-7.661l-6.522 5.025C9.505 39.556 16.227 44 24 44z"/>
    <path fill="#1976D2" d="M43.611 20.083H42V20H24v8h11.303c-.792 2.237-2.231 4.16-4.082 5.571l6.19 5.238C42.021 35.596 44 30.023 44 24c0-1.341-.138-2.65-.389-3.917z"/>
  </svg>
);

const GithubIcon = (props) => (
  <svg viewBox="0 0 16 16" fill="currentColor" {...props}>
    <title>GitHub Profile</title>
    <path d="M8 0C3.58 0 0 3.58 0 8c0 3.54 2.29 6.53 5.47 7.59.4.07.55-.17.55-.38 0-.19-.01-.82-.01-1.49-2.01.37-2.53-.49-2.69-.94-.09-.23-.48-.94-.82-1.13-.28-.15-.68-.52-.01-.53.63-.01 1.08.58 1.23.82.72 1.21 1.87.87 2.33.66.07-.52.28-.87.51-1.07-1.78-.2-3.64-.89-3.64-3.95 0-.87.31-1.59.82-2.15-.08-.2-.36-1.02.08-2.12 0 0 .67-.21 2.2.82.64-.18 1.32-.27 2-.27.68 0 1.36.09 2 .27 1.53-1.04 2.2-.82 2.2-.82.44 1.1.16 1.92.08 2.12.51.56.82 1.27.82 2.15 0 3.07-1.87 3.75-3.65 3.95.29.25.54.73.54 1.48 0 1.07-.01 1.93-.01 2.2 0 .21.15.46.55.38A8.013 8.013 0 0016 8c0-4.42-3.58-8-8-8z" />
  </svg>
);


export default function LoginPage() {
  const [isLoading, setIsLoading] = useState(false);

  const handleSignIn = async (provider) => {
    setIsLoading(true);
    try {
      await signIn(provider, { callbackUrl: "/dashboard" });
    } catch (error) {
      console.error("Sign in failed:", error);
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen w-full flex-col md:flex-row bg-black">
      
      <div className="relative flex w-full flex-col justify-between overflow-hidden bg-black p-8 text-white md:w-1/2 lg:p-12">

        <div className="absolute top-0 left-0 w-3/4 h-3/4 bg-gradient-to-tr from-blue-900/50 via-transparent to-transparent -z-0 opacity-50 blur-3xl"></div>
        <div className="absolute bottom-0 right-0 w-1/2 h-1/2 bg-gradient-to-bl from-purple-900/50 via-transparent to-transparent -z-0 opacity-50 blur-3xl"></div>
                <div className="z-10 flex items-center gap-4">
          <CoinMarketMertLogo className="h-10 w-10 text-white" />
          <h1 className="text-3xl font-bold tracking-tighter">
            CoinMarket<span className="text-blue-400">Mert</span>
          </h1>
        </div>
                <div className="relative z-10">
            <h2 className="text-5xl font-bold leading-tight tracking-tighter text-white lg:text-6xl">
                Track the Market
                <br />
                Master Your Portfolio
            </h2>
            <p className="mt-4 max-w-lg text-lg text-slate-400">
                Real-time crypto and currency data at your fingertips,make smarter investment decisions today.
            </p>
            
            <div className="mt-12">
                <p className="text-sm text-slate-400">Developer</p>
                <a 
                    href="https://github.com/MertGurcuoglu" 
                    target="_blank" 
                    rel="noopener noreferrer" 
                    className="group mt-2 inline-flex items-center gap-4 rounded-lg bg-white/5 p-4 pr-6 transition-colors duration-300 hover:bg-white/10"
                >
                    <GithubIcon className="h-10 w-10 text-white transition-transform duration-300 group-hover:scale-110" />
                    <div>
                        <p className="text-lg font-bold text-white">Mert Gürcüoğlu</p>
                        <p className="text-sm text-slate-400 group-hover:text-blue-400">View GitHub Profile →</p>
                    </div>
                </a>
            </div>
        </div>
        
        <div className="z-10"></div>
      </div>

      <div className="flex w-full items-center justify-center border-t border-slate-800 bg-[#296a9870] p-8 md:w-1/2 md:border-t-0 md:border-l lg:p-12">
        <div className="w-full max-w-sm">
          <h2 className="text-3xl font-bold tracking-tight text-white">
            Welcome Back
          </h2>
          <p className="mt-2 text-slate-200">
            Sign in to access your dashboard
          </p>
          
          <div className="mt-10">
            <button
              onClick={() => handleSignIn("google")}
              disabled={isLoading}
              className="group relative flex h-12 w-full items-center justify-center gap-3 rounded-lg bg-black/20 px-6 font-semibold text-white ring-1 ring-white/20 transition-all duration-300 hover:bg-black/30 hover:shadow-lg disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isLoading ? (
                <div className="h-5 w-5 animate-spin rounded-full border-b-2 border-white"></div>
              ) : (
                <>
                  <GoogleIcon className="h-6 w-6" />
                  <span>Continue with Google</span>
                </>
              )}
            </button>
          </div>
          
          <p className="mt-8 text-center text-xs text-slate-300">
            By continuing, you agree to the Terms of Service and Privacy Policy.
          </p>
        </div>
      </div>
    </div>
  );
}