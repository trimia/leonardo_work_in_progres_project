"use client"

import "./nav.css"
import {IconHome2, IconUserSquare, IconLogout} from "@tabler/icons-react";
import React from "react"
import {usePathname, useRouter} from "next/navigation";
import Cookies from 'js-cookie';
import {AppRouterInstance} from "next/dist/shared/lib/app-router-context.shared-runtime";


interface NavProps {
    tabs: string;
    setTabs: (tab: string) => void;
}

export default function Nav() {
    const pathname: string = usePathname()
    const router: AppRouterInstance = useRouter()

    function checkPath(): boolean {
        let noPath: string[] = ["/", "/login", "/signup",]
        for (let i:number = 0; i < noPath.length; i++) {
            if (pathname === noPath[i]) {
                return false
            }
        }
        return true
    }
    async function logout() {
        const url = "http://localhost:8080/logout"
        try {
            await fetch(url, {
                    method: "POST",
                    headers: {"Content-Type": "application/json",
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    },
                }
            ).then((response) => response.text()).then((responseText: string) => {
                //todo remove console.log
                console.log("logout");
                console.log(responseText);
            }).catch((error) => {
                //todo remove console.log
                console.log(error.toString());
            });
            // Remove the token from cookies
            Cookies.remove('token');
            Cookies.remove('refresh_token');
            // Redirect to the login page or perform any other desired action
            router.push("/");
        } catch (error : any) {
            //todo remove console.log
            console.log(error.toString());
        }
    }

    return (
        <div>
            <nav className="nav">
                <a onClick={()=>Cookies.get("token") === undefined ? router.push("/") : router.push("/home")} className="title">Eventify</a>
                {checkPath() ?
                    <ul>
                        <div className={pathname === "/home" ? "nav_active" : "nav_not_active"}
                             onClick={() => {router.push("/home")}}>Home <IconHome2/>
                        </div>
                        <div className={pathname == "/user" ? "nav_active" : "nav_not_active"}
                             onClick={() => router.push("/user")}>User <IconUserSquare/>
                        </div>
                        <div className={"nav_not_active"}
                             onClick={() => logout()}>logout <IconLogout/>
                        </div>
                    </ul>
                    : null}
            </nav>
            <div className="content-container"></div>
        </div>
    )
}