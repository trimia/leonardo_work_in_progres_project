"use client"
import './user.css'
import {Container} from '@mantine/core';
import React, {useContext, useEffect} from "react";
import {useState} from "react";
import Card_to_modify from "../card_modify/page"
import Cookies from 'js-cookie';
import jwtDecode from "jwt-decode";
import {LoadingContext} from "@/app/LoadingContext";
import {Carousel} from "@mantine/carousel";


interface Cards {
    id: number;
    title: String;
    description: String;
    place: string;
    owner: String;
    datetime: string;
    image: string[];
    tags: String[] | null;
}

interface user {
    firstName: string,
    lastName: string,
    image: string;
    email: string,
    registered: boolean
}



export default function User() {
    const [card, setCard] = useState<Cards[] | null>(null)
    const [userData, setUserData] = useState<user | null>(null);
    const loading = useContext(LoadingContext)

    async function eventLogIn() {
        let access_token: string | undefined = Cookies.get("token");
        if (access_token !== undefined) {
            let jwt: { sub: string, iat: number, exp: number, jti: string } = jwtDecode(access_token);
            let email: string = jwt.sub;
            await fetch(`http://localhost:8080/api/event/myEvents`, {
                    method: "POST",
                    headers: {
                        "Authorization": `Bearer ${access_token}`,
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({"email": email})
                }
            ).then((v) => v.json()).then((v) => {
                setCard(v);
            }).catch()
        }
    }

    async function user_stuff() {
        try {
            await fetch(`http://localhost:8080/api/users/mail`,
                {
                    headers: {
                        "Authorization": `Bearer ${Cookies.get("token")}`
                    },
                }
            ).then((userData) => userData.json()).then((userData) => {
                console.log(userData)
                setUserData(userData);
            }).catch()
        } catch (e: any) {
        }
    }

    useEffect(() => {
        if (!loading) {
            eventLogIn()
            user_stuff()

        }
    }, []);

    return (
        <div className='main'>
            <Container fluid className="user">

                <img className='img' src={userData?.image} alt=''></img>
                <div>
                    <p className='name'>{userData?.firstName} {userData?.lastName}</p>
                </div>
                <div> {userData?.email}</div>

            </Container>

            <div style={{display: "flex", flexWrap: "wrap", justifyContent: "center"}}>
                {Array.isArray(card) && card?.map((card: Cards, id: number) => {
                        return (<Card_to_modify
                                key={id}
                                owner={card.owner}
                                title={card.title}
                                image={card.image}
                                place={card.place}
                                description={card.description}
                                tags={card.tags}
                                datetime={card.datetime}
                                id={card.id}
                            />
                        )
                    }
                )
                }
            </div>
        </div>
    )
}
