"use client"
import {useRouter} from 'next/navigation'
import {Anchor, Button, Checkbox, Container, Group, Paper, PasswordInput, Text, TextInput, Title,} from '@mantine/core';
import {useEffect, useState} from 'react';
import './login.css';
import Cookies from 'js-cookie';
import jwtDecode from "jwt-decode";

export default function Login() {
    const errors = ["user not found",]
    const router = useRouter()
    const [mail, setMail] = useState<string>("")
    const [password, setPassword] = useState<string>("")
    const [forgotPass, setForgotPass] = useState<boolean>(false)
    const [error, setError] = useState<string>("")

    async function sendLogIn(): Promise<void> {
        const url: string = "http://localhost:8080/api/v1/auth/authenticate"
        try {
        const response = await fetch(url, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({"email": mail, "password": password})
        });
        if (response.status !== 200) {
            return;
        }
            const data = await response.json();

            console.log(data)
            // Store the token in cookies
            if (data.access_token) {
                Cookies.set('token', data.access_token, {path:"/", sameSite: "Lax",  expires: jwtDecode(data.access_token).exp }); // Token expires in 1 day
            }
            if(data.refresh_token)
            {
                Cookies.set('refresh_token', data.refresh_token, {path:"/", sameSite: "Lax",  expires: jwtDecode(data.refresh_token).exp });
            }
            // Navigate to the home page or do any other desired action
            router.push("../home");
        } catch (error) {
        }
    }

    async function sendForgotPassword() {
        const url = "http://localhost:8080/forgot_password"
        await fetch(url, {
                method: "POST",
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify({"email": mail})
            }
        ).then((v) => {
            return v.text()
        }).then((v) => {
            if (v === "user not found") {
                setError(v)
            }
        }).catch()
    }

    const sendWithEnter = (e: any) => {
        if (e.key === "Enter") {
            e.preventDefault()
            sendLogIn().catch();
        }
    }

    useEffect(() => {
        document.addEventListener("keydown", sendWithEnter)
        return () => {
            document.removeEventListener("keydown", sendWithEnter)
        }
    }, [])

    return (
        <Container size={420} my={40}>
            <Title
                align="center"
                sx={(theme) => ({fontFamily: `Greycliff CF, ${theme.fontFamily}`, fontWeight: 900})}
            >
                {!forgotPass ? "Welcome back!" : "Reset password"}
            </Title>
            <Text color="dimmed" size="sm" align="center" mt={5}>
                Do not have an account yet?{' '}
                <Anchor size="sm" component="button" onClick={() => router.push('/signup')}>
                    Create account
                </Anchor>
            </Text>
            <Paper withBorder shadow="md" p={30} mt={30} radius="md" className={'paper'}>
                <TextInput error={error === errors[0] ? error : null} value={mail}
                           onChange={(e) => setMail(e.target.value)} label="Email"
                           placeholder="youremail@mantine.dev" required/>
                {!forgotPass &&
                    <PasswordInput
                        // onKeyDown={sendWithEnter}
                                   onChange={(e) => setPassword(e.target.value)} label="Password"
                                   placeholder="Your password" required mt="md"/>}
                <Group position="apart" mt="lg">
                    {/*{!forgotPass ? <Checkbox label="Remember me"/> : <div></div>}*/}
                    <Anchor component="button" size="sm"
                            onClick={() => setForgotPass((prevState: boolean) => !prevState)}>
                        {!forgotPass ? "Forgot password?" : "Login"}
                    </Anchor>
                    {forgotPass && <div></div>}
                </Group>
                {!forgotPass ?
                    <Button onClick={() => sendLogIn()} variant="outline" fullWidth mt="xl">
                        Sign in
                    </Button> :
                    <Button onClick={() => sendForgotPassword()} variant="outline" fullWidth mt="xl" id={"ciao"}>
                        Send password
                    </Button>
                }
            </Paper>
        </Container>
    );
}
