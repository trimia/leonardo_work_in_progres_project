"use client"
import MyCard from "@/app/card";
import {Button, Tabs} from "@mantine/core";
import React, {useContext, useLayoutEffect, useState} from "react";
import {useRouter} from "next/navigation";
import Search from "@/app/home/search";
import Cookies from 'js-cookie';
import {IconConfetti, IconDeviceWatch, IconEdit} from "@tabler/icons-react";
import jwtDecode from "jwt-decode";
import {LoadingContext} from "@/app/LoadingContext";

export interface Cards {
    id: number
    title: string;
    owner: string;
    description: string;
    place: string;
    datetime: string;
    image: string[];
    tags: string[] | null;
}

export default function Home() {
    const [tab, setTab] = useState<string | null>("gallery")
    const router = useRouter()
    const [card, setCard] = useState<Cards[] | null>(null)
    const loading = useContext(LoadingContext)


    // async function deleteUser() {
    //     const url = "http://localhost:8080/api/v1/auth/delete_user";
    //     try {
    //         const response = await fetch(url, {
    //             method: "DELETE",
    //             headers: {
    //                 "Content-Type": "application/json",
    //                 "Authorization": `Bearer ${token?.access_token}`,
    //             },
    //             body: JSON.stringify({"email": mail})
    //         });
    //
    //         if (response.url !== url) {
    //             return null;
    //         }
    //         if (response.status !== 200) {
    //             console.log(response.status + mail + " => ERROR STATUS delete_user");
    //             return null;
    //         }
    //         const data = await response.json();
    //         console.log(data);
    //     } catch (e: any) {
    //         console.log(e.toString());
    //         return null;
    //     }
    // }
    //

    const getAllEvent = async () => {
        try {
            await fetch(`http://localhost:8080/api/event/events`,
                {
                    headers: {
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    },
                }
            ).then((v) => {
                return v.json()
            }).then((v) => {
                setCard(v);
            }).catch()
        } catch (e: any) {
        }
    }

    const getRegisteredEvent = async () => {
        let access_token: string | undefined = Cookies.get("token");
        if (access_token !== undefined) {
            let jwt: { sub: string, iat: number, exp: number, jti: string } = jwtDecode(access_token);
            let email: string = jwt.sub;
            await fetch(`http://localhost:8080/api/event/findEventSubscribed`,
                {
                    method: "POST",
                    headers: {
                        "Authorization": `Bearer ${access_token}`,
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({"email": email})
                }
            ).then((v) => v.json()).then((v) => {
                // console.log(v);
                setCard(v);
                // setOpened(true);
            }).catch()
        }
    }

    useLayoutEffect(() => {
        if(!loading) {
            if (tab === "gallery") {
                getAllEvent()
            } else {
                getRegisteredEvent()
            }
        }
    }, [tab]);

    return (
        <div className="home" style={{position: "relative", height: "100%"}}>
            <Search getAllEvent={getAllEvent} setCard={setCard}></Search>
            <Tabs value={tab} onTabChange={setTab} variant="pills" defaultValue="gallery">
                <Tabs.List className={"tab_list"}>
                    <Tabs.Tab value="gallery" icon={<IconConfetti size="0.8rem"/>}>All Events</Tabs.Tab>
                    <Tabs.Tab value="messages" icon={<IconDeviceWatch size="0.8rem"/>}>Events to attend</Tabs.Tab>
                    <Tabs.Tab onClick={() => router.push('../create_events')} value="create"
                              icon={<IconEdit size="0.8rem"/>}>Create your event</Tabs.Tab>
                </Tabs.List>
                <div style={{display: "flex", flexWrap: "wrap", justifyContent: "center"}}>
                    {Array.isArray(card) && card?.map((card: Cards, id: number) => {
                            return (<MyCard key={id}
                                            place={card.place}
                                            owner={card.owner}
                                            title={card.title}
                                            image={card.image}
                                            description={card.description}
                                            tags={card.tags}
                                            datetime={card.datetime}
                                            id={card.id}/>
                            )
                        }
                    )}
                </div>
                <div className='create-container'>
                    <Button className='create' size='md' variant="filled"
                            onClick={() => router.push('../create_events')}>
                        Create your event!
                        <IconEdit></IconEdit>
                    </Button>
                </div>
            </Tabs>
        </div>
    )
}