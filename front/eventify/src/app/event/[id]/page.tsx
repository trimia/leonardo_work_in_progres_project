"use client"
import {Cards} from "@/app/home/page";
import React, {useContext, useEffect, useState} from "react";
import {ActionIcon, Button, Card, Group, Image, Modal, Text} from "@mantine/core";
import {Carousel} from "@mantine/carousel";
import "./event.css"
import {IconEdit} from "@tabler/icons-react";
import jwtDecode from "jwt-decode";
import Modify_event from "@/app/event/[id]/modify_event";
import {useRouter} from "next/navigation";
import Cookies from "js-cookie";
import {Simulate} from "react-dom/test-utils";
import load = Simulate.load;
import {LoadingContext} from "@/app/LoadingContext";

interface user {
    firstName: string,
    lastName: string,
    email: string,
    registered: boolean
}

export default function Event(params: any | undefined) {
    const [userList, setUserList] = useState<user | null>(null);
    const [event, setEvent] = useState<Cards>({
        id: 0,
        title: "",
        owner: "",
        description: "",
        place: "",
        datetime: "",
        image: [],
        tags: [],
    });
    const [open, setOpen] = useState(false);
    const [formatDate, setFormatDate] = useState<string>()
    const router = useRouter();
    const slides = event.image?.map((url) => (
        <Carousel.Slide key={url}>
            <img alt="" src={url}/>
        </Carousel.Slide>
    ));
    const loading: boolean = useContext(LoadingContext)


    async function get_event() {
        const url = "http://localhost:8080/api/event/findById"
        try {
            await fetch(url, {
                    method: "POST",
                    headers: {
                        "Authorization": `Bearer ${Cookies.get("token")}`,
                        "Content-Type": "application/json"
                    },
                    body: JSON.stringify({"id": params.params.id})
                }
            ).then(async (response) => {
                    return response.json()
                }
            ).then((e: Cards) => {

                let date = new Date(e.datetime)
                setFormatDate(`${date.getDate() < 10 ? "0" + date.getDate() : date.getDate()}/${(date.getMonth() + 1) < 10 ? "0" + (date.getMonth() + 1) : (date.getMonth() + 1)}/${date.getFullYear()} \
    ${date.getHours() < 10 ? "0" + date.getHours() : date.getHours()}:${date.getMinutes() < 10 ? "0" + date.getMinutes() : date.getMinutes()}`)
                setEvent(e);


            }).catch()
        } catch (error: any) {
            console.error("An error occurred while getting the event:", error.toString());
        }
    }

    async function get_list() {
        try {
            const response = await fetch('http://localhost:8080/api/users/findSubscribedUser', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${Cookies.get("token")}`,
                },
                body: JSON.stringify({"id": params.params.id, "registered": true}),
            });
            if (response.ok) {
                const userList = await response.json();
                setUserList(userList);
            } else {
                console.error('Errore nella richiesta:', response.status, response.statusText);
            }
        } catch (e: any) {
            console.error(e.toString());
        }
    }

    useEffect(() => {
        if (!loading) {
            get_event()
            get_list()
        }
    }, []);

    return (
        <>
            <Modal opened={open} onClose={() => setOpen(false)}>
                <Modify_event
                    id={event.id}
                    description={event.description}
                    title={event.title}
                    owner={event.owner}
                    image={event.image}
                    place={event.place}
                    datetime={event.datetime}
                    tags={event.tags}
                />
            </Modal>
            <div className={'cen'}>
                <div className='event'>
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
                            {event?.owner === jwtDecode(Cookies.get("token")).sub &&
                                <ActionIcon style={{zIndex: "10", margin: "-50px 0 0 10px"}}
                                            onClick={() => setOpen(true)}
                                            color="dark" size="lg" variant="outline">
                                    <IconEdit></IconEdit>
                                </ActionIcon>
                            }
                        </Card.Section>

                        <Group mt="md" mb="xs">
                            <Text fw={800} className={"title_event"}>{event?.title}</Text>
                            <Text size="m" className='categ'>
                                {event.tags?.map((tag, index) => (
                                    <span key={index}>#{tag}<>&nbsp;</></span>
                                ))}
                            </Text>
                        </Group>

                        <Text className={"descr_event"} style={{ whiteSpace: 'pre-wrap' }} size={"xl"}>
                            {event?.description}
                        </Text>

                        <br/>

                        <div className={"info_event"}>
                            <Text size="m" className="user_list">
                                Lista di partecipanti:
                                {Array.isArray(userList) ? (
                                    userList.map((user, index) => (
                                        <span key={index}>
                                {user.firstName} {user.lastName}
                                            {index < userList.length - 1 ? ', ' : ''}
                                </span>
                                    ))
                                ) : (
                                    "Nessun partecipante registrato"
                                )}
                            </Text>

                            <br/>

                            <div className="date_event">
                                {formatDate}
                            </div>

                            <br/>

                            <Text size="m" className='place_event'>
                                {event?.place}
                            </Text>

                        </div>
                        <Button onClick={() => router.push("/home")} variant="light" color="blue" fullWidth mt="md"
                                radius="md">
                            Go back to Homepage
                        </Button>
                    </Card>
                </div>
            </div>
        </>
    )
}