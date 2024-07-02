"use client"

interface user {
    firstName : string,
    lastName:string,
    email: string,
    image: string;
    registered: boolean
}

import { useRouter } from 'next/navigation'
import { Card, Image, Text, Badge, Button, Group, TextInput } from '@mantine/core';
import React, {useContext, useEffect, useState} from 'react'
import {Carousel} from "@mantine/carousel";
import {CardProps} from "@/app/cardProps";
import Cookies from "js-cookie";
import jwtDecode from "jwt-decode";

export default function Card_to_modify(props: CardProps){
    const router = useRouter()
    const [registered, setRegistered] = useState(false);
    const [userList, setUserList] = useState<user | null>(null);
    const slides = props.image?.map((url) => (
        <Carousel.Slide key={url}>
            <img alt="" src={url} />
        </Carousel.Slide>
    ));
    const date = new Date(props.datetime)
    // let formatDate = `${props.datetime.getDay()}/${props.datetime.getMonth}/${props.datetime.getFullYear} ${props.datetime.getHours}:${props.datetime.getMinutes}`
    let formatDate = `${date.getDate() < 10 ? "0" + date.getDate() : date.getDate()}/${(date.getMonth()+1) < 10 ? "0" + (date.getMonth()+1) : (date.getMonth()+1)}/${date.getFullYear()} \
    ${date.getHours() < 10 ? "0" + date.getHours() : date.getHours()}:${date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()}`


    async function get_list(){
        try {
            const response = await fetch('http://localhost:8080/api/users/findSubscribedUser', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${Cookies.get("token")}`,
                },
                body: JSON.stringify({"id" : props.id, "registered": true}),
            });
            if (response.ok) {
                const userList = await response.json();
                setUserList(userList);
            }
        } catch (e : any) {

        }
    }

    async function register() {
        if (!registered) {
            const url = "http://localhost:8080/api/event/registerUserToEvent"
            let access_token: string | undefined = Cookies.get("token");
            if (access_token !== undefined) {
                let jwt: { sub: string, iat: number, exp: number, jti: string } = jwtDecode(access_token);
                let email: string = jwt.sub;
                await fetch(url, {
                        method: "POST",
                        headers: {
                            "Authorization": `Bearer ${access_token}`,
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({id: props.id, email: email})
                    }
                ).then((response) => {
                    if (response.status === 200) {
                        return true
                    }
                    if (response.status != 200) {
                        return false
                    }
                    return response.json()
                }).catch()
            }
        }
    }

    async function unregister() {
        if (registered) {
            const url = "http://localhost:8080/api/event/unregisterUserToEvent"
            let access_token: string | undefined = Cookies.get("token");
            if (access_token !== undefined) {
                let jwt: { sub: string, iat: number, exp: number, jti: string } = jwtDecode(access_token);
                let email: string = jwt.sub;
                await fetch(url, {
                        method: "POST",
                        headers: {
                            "Authorization": `Bearer ${access_token}`,
                            "Content-Type": "application/json"
                        },
                        body: JSON.stringify({id: props.id, email: email})
                    }
                ).then( async (response) => {
                    if (response.status === 200) {
                        setRegistered(false);
                    }
                    if (response.status != 200) {
                        return false
                    }
                    await get_list();
                }).catch()
            }
        }
    }

    useEffect(() => {
        get_list()
    }, []);

    async function deleteEvent() {
        const url = "http://localhost:8080/api/event/delete_event";
        try {
            const response = await fetch(url, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${Cookies.get("token")}`,
                },
                body: JSON.stringify({"email": props.owner})
            });
            const data = await response.json();
            console.log(data);
        } catch (e: any) {
            console.log(e.toString());
            return null;
        }
    }

    return(
        <div className='card'>
            <Card shadow="sm" padding="lg" radius="md" withBorder>
                <Card.Section>
                    <Carousel
                        slideSize="50%"
                        slideGap="md"
                        loop
                        height={200}
                        dragFree>
                        {slides}
                    </Carousel>
                </Card.Section>
                <Group mt="md" mb="xs">
                    <Text fw={800} className='title_card'>{props.title}</Text>
                    <Text size="m" className='categ'>
                        {props.tags?.slice(0,3).map((tag, index) => (
                            <span key={index}>#{tag}<>&nbsp;</></span>
                        ))}
                    </Text>
                </Group>

                <Text className={"descr"} truncate="end">
                    {props.description}
                </Text>

                <br/>

                <div className={"info"}>

                    <div className="date">
                        {formatDate}
                    </div>

                    <Text size="m" className='place'>
                        {props.place}
                    </Text>

                </div>
                <Button variant="outline" color="blue" fullWidth radius="md" mt="md" onClick={() => router.push(`/event/${props.id}`)}>
                    SEE MORE
                </Button>

                {registered ? <Button variant="outline" color="red" fullWidth mt="md" radius="md" onClick={() => {
                        unregister();
                        setRegistered(!registered);
                    }}>
                        UNREGISTER
                    </Button> :
                    <Button variant="outline" color="blue" fullWidth mt="md" radius="md" onClick={() => {
                        register();
                        setRegistered(!registered);
                    }}>
                        REGISTER
                    </Button>
                }
                <Button variant="outline" color="red" fullWidth radius="md" mt="md" onClick={() => deleteEvent()}>
                    DELETE EVENT
                </Button>
            </Card>
        </div>
    );
}