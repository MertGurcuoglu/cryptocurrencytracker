import { getToken } from "next-auth/jwt";
import { NextResponse } from "next/server";

export async function middleware(req) {
  const secret = process.env.NEXTAUTH_SECRET;
  
  const token = await getToken({ req, secret });

  const { pathname } = req.nextUrl;

  if (token && pathname === "/login") {
    return NextResponse.redirect(new URL("/dashboard", req.url));
  }

  if (!token && (pathname === "/" || pathname.startsWith("/dashboard"))) {
    return NextResponse.redirect(new URL("/login", req.url));
  }
  
  return NextResponse.next();
}

export const config = {
  matcher: ["/", "/dashboard/:path*", "/login"],
};

