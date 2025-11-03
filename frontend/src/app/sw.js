import { precacheAndRoute } from "@serwist/precaching";
import { registerRoute } from "@serwist/routing";
import { StaleWhileRevalidate } from "@serwist/strategies";

precacheAndRoute(self.__SW_MANIFEST);

self.addEventListener("push", (event) => {
  if (!event.data) {
    console.log("Service Worker: Push event'i geldi ama veri yok.");
    return;
  }
  const data = event.data.json();
  console.log("Service Worker: Push Alındı...");
  self.registration.showNotification(data.title, {
    body: data.body,
    icon: "/favicon.ico",
  });
});
registerRoute(
  ({ url }) => url.pathname.startsWith("/api/") && !url.pathname.endsWith("/stream"),
  new StaleWhileRevalidate({
    cacheName: "api-cache",
    plugins: [
      {
        cacheWillUpdate: async ({ response }) => {
          if (response && response.status < 400) {
            return response;
          }
          return null;
        },
      },
    ],
  })
);

