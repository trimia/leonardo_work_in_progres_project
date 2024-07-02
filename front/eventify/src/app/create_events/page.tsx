"use client"
import {FileInput} from '@mantine/core';
import {DateTimePicker} from '@mantine/dates';
import {useRouter} from 'next/navigation'
import {
    TextInput,
    Paper,
    Title,
    Container,
    Button,
} from '@mantine/core';
import {MultiSelect} from '@mantine/core';
import React, {useState, useEffect, useContext} from "react";
import {useForm} from '@mantine/form';
import {hasLength} from '@mantine/form';
import Cookies from "js-cookie";
import {LoadingContext} from "@/app/LoadingContext";
import {getBase64} from "@/app/base64";

interface formEvent {
    title: string,
    description: string,
    image: Array<File>,
    place: string,
    datetime: Date | null,
    tags: string[],
}

export default function Create_event() {
    const form = useForm<formEvent>({
        initialValues: {
            title: '',
            description: '',
            place: '',
            datetime: null,
            tags: [],
            image: [],
        },
        validate: {
            title: hasLength({min: 2, max: 30}, 'Title must be 2-30 characters long'),
            description: hasLength({min: 2, max: 255}, 'Description must be 2-255 characters long'),
            place: hasLength({min: 2, max: 20}, 'Place must be 2-20 characters long'),
            image: hasLength({min: 1, max: 5}, 'You must upload at least one image'),
        }
    });
    const [tagList, setTagList] = useState<string[]>([])
    const router = useRouter()
    const [dateError, setDateError] = useState<string | null>(null);
    const loading = useContext(LoadingContext)

    async function getTag() {
        const url = "http://localhost:8080/api/event/AllTag";
        try {
            const response = await fetch(url, {
                headers: {
                    "Authorization": `Bearer ${Cookies.get("token")}`
                },
            });

            if (response.ok) {
                const tags = await response.json();
                setTagList(tags);
            }
        } catch (error: any) {
        }
    }

    useEffect(() => {
        if (!loading) {
            getTag()
        }
    }, []);

    useEffect(() => {
        const currentDate = new Date();
        if (form.values.datetime && form.values.datetime <= currentDate) {
            setDateError("Please select a future date for the event.");
        } else {
            setDateError(null);
        }
    }, [form.values.datetime]);

    async function createEvent() {
        if (!form.values.datetime) {
            setDateError("Please select a date and time for the event.");
            return;
        }

        if (dateError) {
            return; // Non inviare i dati se c'è un errore nella data
        }

        if (form.isValid()) {
            const url = "http://localhost:8080/api/event/registerevents"
            let a: string[] = []
            for (let i: number = 0; i < form.values.image.length; i++) {
               a.push(await getBase64(form.values.image[i]).then((v: string): string => {
                   return v
                }))
            }
            fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${Cookies.get("token")}`
                },
                body: JSON.stringify({...form.values, image: a})
            }).then((v) => {
                if (v.url !== url) {
                    return null
                }
                if (v.status !== 200) {
                    return null;
                }
                return v.json()
            }).then((v) => {
            }).catch()
            router.push("/home")
        }
    }

    return (
        <Container  size={420} my={40}>
            <form onSubmit={form.onSubmit(createEvent)}>
                <Title
                    align="center"
                    sx={(theme) => ({fontFamily: `Greycliff CF, ${theme.fontFamily}`, fontWeight: 900})}
                >
                    Add your event
                </Title>
                <Paper withBorder shadow="md" p={30} mt={30} radius="md">
                    <TextInput label="Title" placeholder="Partyyyy" required {...form.getInputProps('title')}/>
                    <TextInput label="Description" placeholder="Festa di pippo"
                               required {...form.getInputProps('description')}/>
                    <FileInput
                        placeholder="Your images gallery"
                        label="Profile picture"
                        accept="image/png,image/jpeg"
                        required
                        multiple
                        {...form.getInputProps('image')}
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
                    />
                    <TextInput label="Place" placeholder="casa di topolino" required {...form.getInputProps('place')}/>
                    <MultiSelect
                        data={tagList}
                        {...form.getInputProps("tags")}
                        clearable
                        searchable
                        creatable
                        getCreateLabel={(value) => `Create "${value}"`}
                        onCreate={(value) => {
                            const newValue: { value: string, label: string } = {value: value, label: value}
                            setTagList([...tagList, value])
                            form.setFieldValue("tags", [...form.values.tags, value])
                            return newValue
                        }}
                        nothingFound={"No tags found"}
                        label={"Tags"}/>
                    <Button variant="outline" mt="sm" type="submit">
                        Create event
                    </Button>
                </Paper>
            </form>
        </Container>
    )
}