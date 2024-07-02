import { useRouter } from 'next/navigation'
import {
  TextInput,
  PasswordInput,
  Checkbox,
  Anchor,
  Paper,
  Title,
  Text,
  Container,
  Group,
  Button,
} from '@mantine/core';
import { useEffect, useLayoutEffect, useState } from 'react';
import User from "eventify/src/pages/user";

interface Test{
  id: number;
  first_name: string;
  last_name: string;
}

export default function Log_in() {
  const router = useRouter()
  const [mail, setMail] = useState<String>("")
  const [password, setPassword] = useState<String>("")
  const [token, setToken] = useState<{access_token : String, refresh_token : String} | null>(null)

  useEffect(() => console.log("mail: ", mail), [mail])
  useEffect(() => console.log("password: ", password), [password])

  var test:Test = {id:0, first_name:"", last_name:""}

  async function sendLogIn(name:String) {
    const url= "http://localhost:8080/api/v1/auth/authenticate"
    try {
      await fetch(url,{
            // redirect : "error",
            method:"POST",
            headers:{"Content-Type":"application/json"},
            body:JSON.stringify({"email":mail,"password":password})
          }
      ).then((v)=> {if (v.url !== url) {
        return null
      } 
       return v.json()}).then((v) => {console.log(v); setToken(v)}).catch()
    } catch (e: any) {
      console.log(e.toString())
    }

  }

  async function signup() {
    const url= "http://localhost:8080/api/v1/auth/register"
    try {
      await fetch(url,{
            method:"POST",
            headers:{"Content-Type":"application/json"},
            body:JSON.stringify({"email":mail,"password":password})
          }
      ).then((v)=> {if (v.url !== url) {
        return null
      }
       return v.json()}).then((v) => {console.log(v); setToken(v)}).catch()
    } catch (e: any) {
      console.log(e.toString())
    }

  }

  async function userLogIn(name:String) {
    try {
      await fetch(`http://localhost:8080/api/users/mail?email=${mail}`,
      {        
        headers : {"Authorization" : `Bearer ${token?.access_token}`}
      }
      ).then((v)=> v.json()).then((v) => console.log(v)).catch()
    } catch (e: any) {
      console.log(e.toString())
    }

  }

  async function eventLogIn(name:String) {
    try {
      await fetch(`http://localhost:8080/api/event/events`,
      // {        
      //   headers : {"Authorization" : `Bearer ${token?.access_token}`}
      // }
      ).then((v)=> v.json()).then((v) => console.log(v)).catch()
    } catch (e: any) {
      console.log(e.toString())
    }

  }

  async function eventByTitle() {
    try {
      await fetch(`http://localhost:8080/api/event/eventTitle`,
      ).then((v)=> v.json()).then((v) => console.log(v)).catch()
    } catch (e: any) {
      console.log(e.toString())
    }

  }

  useEffect(() => console.log(token), [token]);


  return (
    <Container size={420} my={40}>
      <Title
        align="center"
        sx={(theme) => ({ fontFamily: `Greycliff CF, ${theme.fontFamily}`, fontWeight: 900 })}
      >
        Welcome back!
      </Title>
      <Text color="dimmed" size="sm" align="center" mt={5}>
        Do not have an account yet?{' '}
        <Anchor size="sm" component="button" onClick={() => router.push('/signup')}>
          Create account
        </Anchor>
      </Text>

      <Paper withBorder shadow="md" p={30} mt={30} radius="md">
        <TextInput description={mail} onChange={(e) => setMail(e.target.value)} label="Email" placeholder="you@mantine.dev" required />
        <PasswordInput description={password} onChange={(e) => setPassword(e.target.value)} label="Password" placeholder="Your password" required mt="md" />
        <Group position="apart" mt="lg">
          <Checkbox label="Remember me" />
          <Anchor component="button" size="sm">
            Forgot password?
          </Anchor>
        </Group>
        <Button onClick={() => sendLogIn(password)} variant="outline" fullWidth mt="xl">
          Sign in
        </Button>
        <Button onClick={() => userLogIn(password)} variant="outline" fullWidth mt="xl">
          User
        </Button>
        <Button onClick={() => eventLogIn(password)} variant="outline" fullWidth mt="xl">
          Events
          </Button>
        <Button onClick={() => eventByTitle()} variant="outline" fullWidth mt="xl">
          EventByTitle
        </Button>
        <Button onClick={() => signup()} variant="outline" fullWidth mt="xl">
          Sign Up
        </Button>
      </Paper>
    </Container>
  );
}
