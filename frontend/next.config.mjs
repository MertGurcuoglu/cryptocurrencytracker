import serwist from "@serwist/next";

const withSerwist = serwist({
  swSrc: "src/app/sw.js", 
  swDest: "public/sw.js", 
  disable: process.env.NODE_ENV === "development",
});

/** @type {import('next').NextConfig} */
const nextConfig = {};

export default withSerwist(nextConfig);