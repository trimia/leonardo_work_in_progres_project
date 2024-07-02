"use client"
import {FileInput} from '@mantine/core';
import {DateTimePicker} from '@mantine/dates';
import {useRouter} from 'next/navigation'
import {
    TextInput,
    Paper,
    Title,
    Button,
} from '@mantine/core';
import {MultiSelect} from '@mantine/core';
import React, {useState, useEffect, useContext} from "react";
import {useForm} from '@mantine/form';
import {hasLength} from '@mantine/form';
import {Cards} from "@/app/home/page";
import { Notifications } from '@mantine/notifications';
import Cookies from "js-cookie";

interface formEvent {
    id: number,
    title: string,
    description: string,
    place: string,
    datetime: Date | null,
    tags: string[] | null,
}

export default function Modify_event(props: Cards) {
    const form = useForm<formEvent>({
        initialValues: {
            id: props.id,
            title: props.title,
            description: props.description,
            place: props.place,
            datetime: new Date(props.datetime),
            tags: props.tags,
        },
        validate: {
            title: hasLength({min: 2, max: 30}, 'Title must be 2-30 characters long'),
            description: hasLength({min: 2, max: 255}, 'Description must be 2-255 characters long'),
            place: hasLength({min: 2, max: 20}, 'Place must be 2-20 characters long'),
        }
    });
    const [tagList, setTagList] = useState<string[]>(props.tags || []);
    const router = useRouter()
    const [dateError, setDateError] = useState<string | null>(null);

    useEffect(() => {
        const currentDate = new Date();
        if (form.values.datetime && form.values.datetime <= currentDate) {
            setDateError("Please select a future date for the event.");
        } else {
            setDateError(null);
        }
    }, [form.values.datetime]);

    async function createEvent() {
        if (dateError) {
            console.log("Date validation error:", dateError);
            return; // Non inviare i dati se c'è un errore nella data
        }
        console.log(form.values)
        if (form.isValid()) {
            const url = "http://localhost:8080/api/event/upgradeEvent"
            try {
                await fetch(url, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            "Authorization": `Bearer ${Cookies.get("token")}`
                        },
                        body: JSON.stringify(form.values)
                    }
                ).then((v) => {
                    console.log(v)
                    if (v.url !== url) {
                        return null
                    }
                    if (v.status !== 200) {
                        console.log(v.status + " => ERROR STATUS")
                        return null;
                    }
                    return v.json()
                }).then((v) => {
                    console.log(v)
                    if (v) {
                        // Invia una notifica se la modifica dell'evento ha avuto successo
                        const notification = {
                            title: "Evento modificato",
                            message: "La modifica dell'evento è stata completata con successo!",
                        };
                        Notifications.show(notification);
                    }
                }).catch()
            } catch (e: any) {
                console.log(e.toString())
            }
            router.push("/home")
        }
    }

    return (
            <form onSubmit={form.onSubmit(console.log)}>
                <Title                    align="center"
                    sx={(theme) => ({fontFamily: `Greycliff CF, ${theme.fontFamily}`, fontWeight: 900})}
                >
                    Modify your event
                </Title>
                <Paper withBorder shadow="md" p={30} mt={30} radius="md">
                    <TextInput label="Title" placeholder={props.title} required {...form.getInputProps('title')}/>
                    <TextInput label="Description" placeholder={props.description}
                               required {...form.getInputProps('description')}/>
                    <FileInput
                        placeholder="Your image"
                        label="Profile picture"
                        accept="image/png,image/jpeg"
                        multiple
                    />
                    <DateTimePicker
                        clearable
                        {...form.getInputProps('datetime')}
                        valueFormat="DD MMM YYYY HH:mm"
                        label="Date of event"
                        maxDate={new Date(new Date().getFullYear() + 1, 11, 31)} // Optional: Set a maximum date (e.g., one year in the future)
                        error={dateError}
                        maw={400}
                        mx="auto"
                        required
                    />
                    <TextInput label="Place" placeholder={props.place} required {...form.getInputProps('place')}/>
                    <MultiSelect
                        data={tagList}
                        {...form.getInputProps("tags")}
                        clearable
                        searchable
                        creatable
                        getCreateLabel={(value) => `Create "${value}"`}
                        onCreate={(value) => {
                            const newValue: {value: string, label: string} = {value: value, label: value}
                            setTagList([...tagList, value])
                            form.setFieldValue("tags", form.values.tags !== null ?[...form.values.tags, value]:[])
                            return newValue
                        }}
                        nothingFound={"No tags found"}
                        label={"Tags"}/>
                    <Button variant="outline" mt="sm" onClick={createEvent}>
                        Modify event
                    </Button>
                </Paper>
            </form>
    )
}