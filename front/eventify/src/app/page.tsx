"use client"
import './landing.css'
import { Button, Group, HoverCard } from '@mantine/core';
import { useRouter } from 'next/navigation'
import { Text } from '@mantine/core';

export default function Landing(props: any | undefined){
    {
        const router = useRouter()
        return (
            <div className='back'>
                <div className="left">
                    <div className='welcome'>
                        <Text
                            fw={1200}
                            variant="gradient"
                            gradient={{ from: 'red', to: 'yellow', deg: 180 }}>
                            Welcome<br/> to<br/> Eventify
                        </Text>
                    </div>
                </div>
                <div className='right'>
                    <div className='description'>
                        <Text
                            fw={1200}
                            variant="gradient"
                            gradient={{ from: 'red', to: 'yellow', deg: 180 }}>
                            Create Event
                        </Text>
                        <Text
                            fw={1200}
                            variant="gradient"
                            gradient={{ from: 'red', to: 'yellow', deg: 180 }}>
                            or partecipate!
                        </Text>
                        <Text
                            fw={1200}
                            variant="gradient"
                            gradient={{ from: 'red', to: 'yellow', deg: 180 }}>
                            And Have fun!!
                        </Text>
                    </div>
                    <div className='cont'>
                        <div className='but'>
                            <Group>
                                <HoverCard width={280} shadow="md">
                                    <HoverCard.Target>
                                        <Button
                                            className="log"
                                            onClick={() => router.push('/login')}
                                            size="compact-xl"
                                            variant="gradient">
                                            Login
                                        </Button>
                                    </HoverCard.Target>
                                    <HoverCard.Dropdown>
                                        <Text size="m">
                                            If you already have an account, click here.
                                        </Text>
                                    </HoverCard.Dropdown>
                                </HoverCard>
                            </Group>
                            <Group>
                                <HoverCard width={280} shadow="md">
                                    <HoverCard.Target>
                                        <Button
                                            className="signu"
                                            onClick={() => router.push('/signup')}
                                            size="compact-xl"
                                            variant="gradient">
                                            Sign up
                                        </Button>
                                    </HoverCard.Target>
                                    <HoverCard.Dropdown>
                                        <Text size="m">
                                            If you don't have an account yet, click here.
                                        </Text>
                                    </HoverCard.Dropdown>
                                </HoverCard>
                            </Group>
                            <Group>
                                <HoverCard width={280} shadow="md">
                                    <HoverCard.Target>
                                        <Button
                                            className="learn"
                                            size="compact-xl"
                                            variant="gradient">
                                            Learn more
                                        </Button>
                                    </HoverCard.Target>
                                    <HoverCard.Dropdown>
                                        <Text size="m">
                                            Eventify is a web application that allows you to organize and partecipate to events.
                                            You can see all the other people who partecipate, the type of the event and even more.
                                            You must be at least 18 years old in order to use the app.
                                        </Text>
                                    </HoverCard.Dropdown>
                                </HoverCard>
                            </Group>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}
