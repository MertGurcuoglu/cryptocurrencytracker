"use client";

import { useEffect, useState, useRef } from "react";
import { useSession, signOut } from "next-auth/react";
import NextImage from "next/image";
import { EventSourcePolyfill } from "event-source-polyfill";

const CoinMarketMertLogo = (props) => (
  <svg viewBox="0 0 40 40" fill="none" xmlns="http://www.w3.org/2000/svg" {...props}><title>CoinMarketMert Logo</title><circle cx="20" cy="20" r="18" stroke="currentColor" strokeWidth="2.5" /><path d="M10 23L15.5 17L22 24L30 16" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" /></svg>
);
const PriceChange = ({ percentage }) => {
    if (percentage === null || percentage === undefined) return <span className="text-slate-500">-</span>;
    const isPositive = percentage >= 0;
    return (
        <span className={`font-semibold ${isPositive ? 'text-emerald-600' : 'text-red-600'}`}>{isPositive ? '▲' : '▼'} {Math.abs(percentage).toFixed(2)}%</span>
    );
};
const StarIcon = ({ filled, onClick }) => (
    <svg onClick={onClick} className={`w-6 h-6 flex-shrink-0 cursor-pointer transition-transform duration-200 hover:scale-125 ${filled ? "text-yellow-400" : "text-slate-300 hover:text-slate-400"}`} fill={filled ? "currentColor" : "none"} stroke="currentColor" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.196-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.783-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z"></path></svg>
);
const SearchIcon = (props) => (
    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" {...props}><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
);
const LogOutIcon = (props) => (
  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" {...props}><path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path><polyline points="16 17 21 12 16 7"></polyline><line x1="21" y1="12" x2="9" y2="12"></line></svg>
);
const formatPrice = (price) => {
    if (price === null || price === undefined) return "$0.00";
    if (price < 10) return `$${price.toFixed(4)}`;
    return `$${price.toFixed(2)}`;
};
const formatLargeNumber = (num) => {
    if (num === null || num === undefined) return "N/A";
    if (num >= 1_000_000_000_000) return `$${(num / 1_000_000_000_000).toFixed(2)}T`;
    if (num >= 1_000_000_000) return `$${(num / 1_000_000_000).toFixed(2)}B`;
    if (num >= 1_000_000) return `$${(num / 1_000_000).toFixed(2)}M`;
    return `$${num.toLocaleString('en-US')}`;
};
function urlBase64ToUint8Array(base64String) {
    if (!base64String) return new Uint8Array();
    const padding = "=".repeat((4 - (base64String.length % 4)) % 4);
    const base64 = (base64String + padding).replace(/-/g, "+").replace(/_/g, "/");
    const rawData = window.atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) { outputArray[i] = rawData.charCodeAt(i); }
    return outputArray;
}
const clearFavoritesCache = async () => {
    try {
        if ("caches" in window) {
            const cache = await caches.open("api-cache");
            const apiUrl = process.env.NEXT_PUBLIC_API_URL;
            await cache.delete(`${apiUrl}/api/favorites`);
            console.log("Favoriler önbelleği başarıyla temizlendi.");
        }
    } catch (error) {
        console.error("Favoriler önbelleği temizlenirken hata oluştu:", error);
    }
};

export default function DashboardPage() {
    const { data: session, status } = useSession();
    const [currencies, setCurrencies] = useState([]);
    const [favorites, setFavorites] = useState(new Set());
    const [isLoading, setIsLoading] = useState(true);
    const [isConnected, setIsConnected] = useState(false);
    const [filter, setFilter] = useState('all');
    const [searchTerm, setSearchTerm] = useState('');
    const eventSourceRef = useRef(null);

    const apiUrl = process.env.NEXT_PUBLIC_API_URL;

    const handleSignOut = async () => {
        console.log("Çıkış yapılıyor ve PWA önbellekleri temizleniyor...");
        if ("caches" in window) { await caches.delete("api-cache"); console.log("API önbelleği (api-cache) başarıyla silindi."); }
        if ("serviceWorker" in navigator) { const registration = await navigator.serviceWorker.getRegistration(); if (registration) { await registration.unregister(); console.log("Service Worker kaydı başarıyla kaldırıldı."); } }
        signOut({ callbackUrl: "/login", redirect: true });
    };

    const handleFavoriteToggle = async (currencySymbol) => {
        if (!session?.backendJwt) return;
        const isFavorite = favorites.has(currencySymbol);
        const originalFavorites = new Set(favorites);
        const newFavorites = new Set(originalFavorites);
        if (isFavorite) {
            newFavorites.delete(currencySymbol);
        } else {
            newFavorites.add(currencySymbol);
        }
        setFavorites(newFavorites);

        try {
            const method = isFavorite ? "DELETE" : "POST";
            const url = isFavorite ? `${apiUrl}/api/favorites/${currencySymbol}` : `${apiUrl}/api/favorites`;
            const body = isFavorite ? null : JSON.stringify({ currencyId: currencySymbol });
            
            const res = await fetch(url, {
                method,
                headers: { "Content-Type": "application/json", Authorization: `Bearer ${session.backendJwt}` },
                body,
            });

            if (res.ok) {
                await clearFavoritesCache();
            } else {
                setFavorites(originalFavorites);
            }
        } catch (error) {
            setFavorites(originalFavorites);
        }
    };

    useEffect(() => {
        if (status !== 'authenticated' || !session?.backendJwt) {
            setIsLoading(status === 'loading');
            return;
        }
        
        const initializeDashboard = async () => {
            console.log(`Dashboard ${session.user.email} için başlatılıyor...`);
            setIsLoading(true);

            try {
                const [favRes, currenciesRes] = await Promise.all([
                    fetch(`${apiUrl}/api/favorites`, { headers: { Authorization: `Bearer ${session.backendJwt}` } }),
                    fetch(`${apiUrl}/api/currencies/all-values`, { headers: { Authorization: `Bearer ${session.backendJwt}` } })
                ]);

                if (favRes.ok) {
                    const favoriteIds = await favRes.json();
                    setFavorites(new Set(favoriteIds));
                }
                if (currenciesRes.ok) {
                    const initialCurrencies = await currenciesRes.json();
                    setCurrencies(initialCurrencies);
                }
            } catch (error) { 
                console.error("Başlangıç verileri alınamadı:", error);
            } finally {
                setIsLoading(false); 
            }
            
            if (eventSourceRef.current) {
                eventSourceRef.current.close();
            }
            const es = new EventSourcePolyfill(`${apiUrl}/api/currencies/stream`, { headers: { Authorization: `Bearer ${session.backendJwt}` }, heartbeatTimeout: 45000 });
            
            es.onopen = () => setIsConnected(true);
            
            const handleEvent = (event) => {
                try {
                    const payload = JSON.parse(event.data);
                    if (event.type === 'update' && payload && Array.isArray(payload.currencies)) {
                        setCurrencies(payload.currencies); 
                        setApiStatus(payload.status);
                    }
                } catch (err) { /* no-op */ }
            };
            
            es.addEventListener("update", handleEvent);
            es.onerror = () => setIsConnected(false);
            eventSourceRef.current = es;

            if ("Notification" in window && "serviceWorker" in navigator && Notification.permission !== "denied") {
                const permission = await Notification.requestPermission();
                if (permission === 'granted') {
                    await registerPushSubscription();
                }
            }
        };
        
        const registerPushSubscription = async () => {
            try {
                const registration = await navigator.serviceWorker.ready;
                const existingSubscription = await registration.pushManager.getSubscription();
                
                if (existingSubscription) {
                    console.log("Zaten mevcut bir bildirim aboneliği var.");
                    return;
                }

                const applicationServerKey = urlBase64ToUint8Array(process.env.NEXT_PUBLIC_VAPID_PUBLIC_KEY);
                const subscription = await registration.pushManager.subscribe({
                    userVisibleOnly: true,
                    applicationServerKey
                });
                
                console.log("Yeni bildirim aboneliği oluşturuldu, backend'e gönderiliyor...");
                await fetch(`${apiUrl}/api/notifications/subscribe`, {
                    method: 'POST', body: JSON.stringify(subscription), headers: { 'Content-Type': 'application/json', 'Authorization': `Bearer ${session.backendJwt}` }
                });
            } catch (error) {
                console.error('Push aboneliği oluşturulurken hata oluştu:', error);
            }
        };

        initializeDashboard();

        return () => {
            if (eventSourceRef.current) {
                eventSourceRef.current.close();
            }
        };
    }, [session, status, apiUrl]);

    const filteredCurrencies = currencies
        .filter(currency => {
            if (filter === 'favorites') {
                return favorites.has(currency.symbol);
            }
            return true;
        })
        .filter(currency => {
            if (searchTerm.trim() === '') {
                return true;
            }
            const term = searchTerm.toLowerCase();
            const name = currency.fullName ? currency.fullName.toLowerCase() : '';
            const symbol = currency.symbol ? currency.symbol.toLowerCase() : '';
            return name.includes(term) || symbol.includes(term);
        });
    
    return (
        <div className="min-h-screen bg-slate-100">
            <header className="sticky top-0 z-50 flex h-16 items-center justify-between gap-4 bg-slate-900 px-4 text-white shadow-md sm:px-6 md:px-8">
                <div className="flex items-center gap-3">
                    <CoinMarketMertLogo className="h-7 w-7 text-white" />
                    <div className="text-xl font-bold tracking-tighter">
                        CoinMarket<span className="text-blue-400">Mert</span>
                    </div>
                </div>
                <div className="relative hidden md:block flex-1 max-w-md mx-4">
                    <SearchIcon className="absolute left-3.5 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-400" />
                    <input 
                        type="text" 
                        placeholder="Search by name or symbol..." 
                        className="w-full rounded-lg border border-slate-700 bg-slate-800 py-2 pl-10 pr-4 text-white transition-colors placeholder:text-slate-400 focus:border-blue-500 focus:bg-slate-700 focus:outline-none focus:ring-1 focus:ring-blue-500"
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />
                </div>
                <div className="flex items-center gap-4">
                    <div className="hidden flex-col items-end text-right sm:flex">
                        <span className="text-sm font-semibold text-white">{session?.user?.email}</span>
                        <div className="flex items-center gap-3 text-xs text-slate-400">
                            <div className="flex items-center" title="Connection Status">
                                <span className={`h-2 w-2 rounded-full mr-1.5 transition-colors ${isConnected ? "bg-green-400" : "bg-red-500 animate-pulse"}`}></span>
                                {isConnected ? "Connected" : "Not Connected"}
                            </div>
                        </div>
                    </div>
                    <button onClick={handleSignOut} title="Sign Out" className="flex-shrink-0 h-10 w-10 rounded-full bg-slate-800 text-slate-400 transition-colors hover:bg-slate-700 hover:text-white focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2 focus:ring-offset-slate-900 flex items-center justify-center">
                        <LogOutIcon className="h-5 w-5" />
                    </button>
                </div>
            </header>
            <main className="p-4 sm:p-6 md:p-8">
                <div className="mb-6 flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center">
                    <h1 className="text-3xl font-bold text-slate-900">Market Overview</h1>
                    <div className="flex space-x-1 rounded-lg bg-slate-200 p-1">
                        <button onClick={() => setFilter('all')} className={`px-4 py-1.5 text-sm font-semibold rounded-md transition-colors ${filter === 'all' ? 'bg-white text-slate-800 shadow' : 'bg-transparent text-slate-500 hover:text-slate-700'}`}>ALL</button>
                        <button onClick={() => setFilter('favorites')} className={`px-4 py-1.5 text-sm font-semibold rounded-md transition-colors ${filter === 'favorites' ? 'bg-white text-slate-800 shadow' : 'bg-transparent text-slate-500 hover:text-slate-700'}`}>Favorites</button>
                    </div>
                </div>
                {isLoading ? (
                    <div className="flex justify-center items-center h-96">
                        <div className="text-center text-slate-500">
                            <svg className="animate-spin h-8 w-8 text-blue-500 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24"><circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle><path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path></svg>
                            <p className="text-lg font-semibold">Loading Market Data...</p>
                            <p className="text-sm">Please wait a moment.</p>
                        </div>
                    </div>
                ) : (
                    <div className="overflow-x-auto rounded-lg border border-slate-200 bg-white shadow-sm">
                        <table className="w-full">
                            <thead className="border-b border-slate-200 bg-slate-50 text-xs text-slate-500 uppercase">
                                <tr>
                                    <th className="px-6 py-3 text-left font-medium"></th><th className="px-6 py-3 text-center font-medium">#</th>
                                    <th className="px-6 py-3 text-left font-semibold">Name</th><th className="px-6 py-3 text-right font-semibold">Price</th>
                                    <th className="px-6 py-3 text-right font-semibold">24h %</th><th className="px-6 py-3 text-right font-semibold hidden lg:table-cell">Market Cap</th>
                                    <th className="px-6 py-3 text-right font-semibold hidden lg:table-cell">Volume(24h)</th><th className="px-6 py-3 text-right font-semibold hidden xl:table-cell">Circulating Supply</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-slate-200">
                                {filteredCurrencies.length > 0 ? (
                                    filteredCurrencies.map((c, index) => (
                                        <tr key={c.id || c.symbol} className="hover:bg-slate-50 transition-colors">
                                            <td className="px-6 py-4"><StarIcon filled={favorites.has(c.symbol)} onClick={() => handleFavoriteToggle(c.symbol)} /></td>
                                            <td className="px-6 py-4 text-center font-medium text-slate-500">{index + 1}</td>
                                            <td className="px-6 py-4">
                                                <div className="flex items-center gap-3">
                                                    {c.logoUrl && (<NextImage src={c.logoUrl} alt={`${c.fullName} logo`} width={32} height={32} className="rounded-full" unoptimized />)}
                                                    <div>
                                                        <p className="font-semibold text-slate-900">{c.fullName}</p>
                                                        <p className="text-sm text-slate-500 uppercase">{c.symbol}</p>
                                                    </div>
                                                </div>
                                            </td>
                                            <td className="px-6 py-4 text-right font-medium text-slate-800">{formatPrice(c.currentPrice)}</td>
                                            <td className="px-6 py-4 text-right"><PriceChange percentage={c.priceChangePercentage24h} /></td>
                                            <td className="px-6 py-4 text-right font-medium text-slate-600 hidden lg:table-cell">{formatLargeNumber(c.marketCap)}</td>
                                            <td className="px-6 py-4 text-right font-medium text-slate-600 hidden lg:table-cell">{formatLargeNumber(c.totalVolume)}</td>
                                            <td className="px-6 py-4 text-right font-medium text-slate-600 hidden xl:table-cell">{c.circulatingSupply ? `${c.circulatingSupply.toLocaleString('en-US', {maximumFractionDigits: 0})} ${c.symbol.toUpperCase()}` : 'N/A'}</td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan={8} className="text-center py-16 text-slate-500">
                                            <p className="font-semibold">{filter === 'favorites' ? 'You have not added any favorites yet.' : (searchTerm ? `No results found for "${searchTerm}"` : 'No currencies to display.')}</p>
                                            <p className="mt-2 text-sm">{filter === 'favorites' && 'Click the star icon next to a currency to add it to your favorites.'}</p>
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}
            </main>
        </div>
    );
}

