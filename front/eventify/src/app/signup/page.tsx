"use client"
import {FileInput} from '@mantine/core';
import {DateInput} from '@mantine/dates';
import {useRouter} from 'next/navigation'
import {
    TextInput,
    PasswordInput,
    Paper,
    Title,
    Container,
    Button,
} from '@mantine/core';
import React, {useEffect, useState} from "react";
import {useForm} from '@mantine/form';
import {hasLength} from '@mantine/form';
import "./signup.css"
import {getBase64} from "@/app/base64";
import Cookies from "js-cookie";

// create a function that calculate if you have 18 years old from the date of birth check day, month and year
function calculateAge(birthday: Date) {
    birthday.setHours(0)
    birthday.setMinutes(0)
    birthday.setSeconds(0)
    birthday.setMilliseconds(0)
    const ageDifMs = Date.now() - birthday.getTime();
    const ageDate = new Date(ageDifMs);
    return Math.abs(ageDate.getUTCFullYear() - 1970);
}

function getFormData() {
    return {
        firstname: '',
        lastname: '',
        email: '',
        password: '',
        date_of_birth: new Date(),
        image: ""
    };
}

export default function Sign_up() {
    const router = useRouter()
    const [error, setError] = useState<string | null>(null);
    const form = useForm<{
        firstname: string,
        lastname: string,
        email: string,
        password: string,
        date_of_birth: Date,
        image: File
    }>({
        initialValues: getFormData(),
        // functions will be used to validate values at corresponding key
        validate: {
            firstname: hasLength({min: 2, max: 15}, 'Name must be 2-15 characters long'),
            lastname: hasLength({min: 2, max: 15}, 'Name must be 2-15 characters long'),
            // image: hasLength({min: 1, max: 5}, 'You must upload at least one image'),
            // image: (image: Array<File>) => {
            //     if (image.length !== 2) {
            //         return 'Devi caricare esattamente una immagine';
            //     }
            //     return null;},
            email: (value: string) => (/^\S+@\S+$/.test(value) ? null : 'Invalid email'),
            date_of_birth: (dateOfBirth: Date) => {
                return calculateAge(dateOfBirth) >= 18 ? null : 'Devi avere almeno 18 anni per registrarti';
            },
            password: (value) => (
                /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$&+,:;=?@#|'<>.^*()%!-]).{6,}$/.test(value)
                    ? null
                    : 'Invalid password: at least 6 characters, one Uppercase letter, one number and one special character'
            ),
        }
    });

    async function signup() {
        if (form.isValid()) {
            const url = "http://localhost:8080/api/v1/auth/register"
            let a: string = await getBase64(form.values.image)
            try {
                await fetch(url, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                            // "Authorization": `Bearer ${Cookies.get("token")}`
                        },
                        body: JSON.stringify({...form.values, image: a})
                    }
                ).then((v) => {
                    if (v.url !== url) {
                        return null
                    }
                    return v.json()
                }).then((v) => {
                    if (v.error === "mail already present") {
                        setError("mail already present")
                    }
                    if (v.error === null) {
                        router.push("/login")
                    }
                }).catch(error => {
                        setError("Email already being used");
                    }
                )
            } catch (e: any) {
            }
        }
    }

    const sendWithEnter = (e: any) => {
        if (e.key === "Enter") {
            e.preventDefault()
            signup().catch();
        }
    }

    useEffect(() => {
        document.addEventListener("keydown", sendWithEnter)
        return () => {
            document.removeEventListener("keydown", sendWithEnter)
        }
    }, [])

    return (
        <div className='sign'>
            <Container size={420} my={40}>
                <form onSubmit={form.onSubmit(signup)}>
                    <Title
                        align="center"
                        sx={(theme) => ({fontFamily: `Greycliff CF, ${theme.fontFamily}`, fontWeight: 900})}
                    >
                        Welcome!
                    </Title>
                    <Paper withBorder shadow="md" p={30} mt={30} radius="md" className={'paper'}>
                        <TextInput label="First name" placeholder="Franco"
                                   required {...form.getInputProps('firstname')}/>
                        <TextInput label="Last Name" placeholder="Pippo" required {...form.getInputProps('lastname')}/>
                        <FileInput
                            placeholder="Your image"
                            label="Profile picture"
                            accept="image/png,image/jpeg"
                            required
                            {...form.getInputProps('image')}
                        />
                        <DateInput
                            valueFormat="DD MMM YYYY"
                            label="Date of birth"
                            placeholder="Date input"
                            description="Must be at least 18 years old"
                            maw={400}
                            mx="auto"
                            {...form.getInputProps('date_of_birth')}
                            required
                        />
                        <TextInput label="Email" placeholder="you@gmail.com" required {...form.getInputProps('email')}/>
                        {error && <div className="error">{error} </div>}
                        <div className='Password'>
                            <PasswordInput
                                required {...form.getInputProps('password')}
                                label="Your password"
                                placeholder="Your password"
                            />
                        </div>
                        <Button variant="outline" mt="sm" type="submit" onClick={() => signup()}>
                            Sign up
                        </Button>
                    </Paper>
                </form>
            </Container>
        </div>
    )
}
