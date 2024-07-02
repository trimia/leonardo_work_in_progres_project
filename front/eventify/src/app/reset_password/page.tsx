"use client"
import React, {useEffect, useState} from "react"
import {useRouter} from "next/navigation";
import {getURL} from "next/dist/shared/lib/utils";
import {
    Button,
    Container,
    Loader,
    PasswordInput,
    Title
} from "@mantine/core";


export default function ResetPassword() {
    const [tempToken, setTempToken] = useState<string | null>(null)
    const [password, setPassword] = useState<string>()
    const router = useRouter()
    const param = getURL().slice(getURL().indexOf("?") + 1)
    const [error, setError] = useState<boolean>(false)
    const [counter, setCounter] = useState<number>(-1)

    //todo: inserire controlli password
    async function sendUrlToken() {
        let urlToken: string = param.slice(param.indexOf("=") + 1)
        const url: string = "http://localhost:8080/test_p"
        await fetch(url, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({"token": urlToken})
        }).then((r: Response): Promise<string> | null => {
            if (r.status === 400 || r.status === 500) {
                return null
            }
            return r.text()
        }).then((r: string | null) => {
                console.log("r:" + r)
                if (r !== null) {
                    setTempToken(r)
                } else {
                    setTempToken("")
                    setError(true)
                }
            }
        )
    }

    async function sendNewPassword() {
        const url: string = "http://localhost:8080/reset_password"
        await fetch(url, {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({"token": tempToken, "password": password})
        }).then((r: Response) => {
            if (r.status !== 200) {
                console.log(r.status)
            }
        }).catch()
    }

    useEffect(() => {
        sendUrlToken().then((v) => {
        })
        console.log()
    }, []);

    //todo eliminare
    useEffect(() => {
        console.log(tempToken)
        console.log(error)
    }, [tempToken]);


    useEffect(() => {
        setCounter(5)
    }, [error]);

    useEffect(() => {
        counter > 0 && setTimeout(() => setCounter(counter - 1), 1000);
        if (counter === 0)
            router.push("/login")
    }, [counter]);


    return (
        tempToken === null ?
            <center>
                <Loader color="orange" size="xl"/>
            </center>
            :
            (tempToken !== "" && !error) ?
                <Container size={420} my={40}>
                    <Title
                        align="center"
                        sx={(theme) => ({fontFamily: `Greycliff CF, ${theme.fontFamily}`, fontWeight: 900})}
                    >
                        Set new password
                    </Title>
                    <PasswordInput value={password} onChange={(e) => setPassword(e.target.value)} label="Password"
                                   placeholder="Your password" required mt="md"/>
                    {/*<PasswordInput label="Password"*/}
                    {/*               placeholder="Your password" required mt="md"/>*/}
                    <Button onClick={sendNewPassword} variant="outline" fullWidth mt="xl">
                        Send password
                    </Button>
                </Container>
                :
                <Container size={420} my={40}>
                    <Title
                        style={{color: "red"}}
                        align="center"
                        sx={(theme) => ({fontFamily: `Greycliff CF, ${theme.fontFamily}`, fontWeight: 900})}
                    >
                        Invalid request
                    </Title>
                    <div>redirect {counter}</div>
                    <Button onClick={() => router.push('/login')}>return to LogIn</Button>
                </Container>

    )
}
