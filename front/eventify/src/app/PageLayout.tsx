"use client"
import React, {useLayoutEffect, useState} from "react";
import Token from "@/app/Token";
import Nav from "@/app/nav";
import {Loader, MantineProvider} from '@mantine/core';
import {Notifications} from '@mantine/notifications';
import Cookies from "js-cookie";
import {usePathname, useRouter} from "next/navigation";
import jwtDecode from "jwt-decode";
import {LoadingContext} from "@/app/LoadingContext";

export default function PageLayout({
                                       children,
                                   }: {
    children: React.ReactNode
}) {
    const pathname = usePathname();
    const [loader, setLoader] = useState<boolean>(true)
    const router = useRouter()

    async function tokenExpired(jwt: Token) {
        const url: string = "http://localhost:8080/api/event/exp"
        const res: Response = await fetch(url, {
            headers: {
                "Authorization": `Bearer ${jwt.access_token}`
            }
        })
        console.log(res.text())
        if (!res.ok) {
            refreshToken(jwt).catch()
        }
        setLoader(false)
    }

    async function refreshToken(jwt: Token) {
        const res: Response = await fetch("http://localhost:8080/api/v1/auth/refresh-token", {
            headers: {
                "Authorization": `Bearer ${jwt.refresh_token}`
            },
        })
        Cookies.remove('token');
        Cookies.remove('refresh_token');
        console.log(res)
        if (res.ok && res.url === "http://localhost:8080/api/v1/auth/refresh-token") {
            console.log("refreshed")
            const token: Token = await res.json();
            Cookies.set('token', token.access_token, {path: "/", sameSite: "Lax", expires: jwtDecode(token.access_token).exp}); // Token expires in 1 day
            Cookies.set('refresh_token', token.refresh_token, {path: "/", sameSite: "Lax", expires: jwtDecode(token.refresh_token).exp});
            setLoader(false)
        } else {
            router.push("/login")
        }
    }

    function check() {
        setLoader(true)
        const path = pathname
        const protectedRoutes: string[] = ["/create_event", "/home", "/settings", "/user", "/event/*"];
        if (protectedRoutes.includes(path)) {
            if (!Cookies.get("token") && !Cookies.get("refresh_token")) {
                router.push("/login")
            } else {
                console.log("cookie exist")
                let acces_token: string | undefined = Cookies.get("token")
                let refresh_token: string | undefined = Cookies.get("refresh_token")
                if (acces_token && refresh_token) {
                    console.log("refr")
                    tokenExpired({access_token: acces_token, refresh_token: refresh_token}).catch()
                }
                setLoader(false)
            }
        }
        setLoader(false)
    }

    useLayoutEffect(() => {
        check()
    }, [pathname]);

    return (
        <MantineProvider>
            <Notifications autoClose={4000}/>
            <Nav></Nav>
            <LoadingContext.Provider value={loader}>
                {loader ? <center><Loader color="orange" size="xl"/></center> :
                    children}
            </LoadingContext.Provider>
        </MantineProvider>
    )
}