import {Button, Container, MultiSelect, TextInput} from "@mantine/core";
import {DateInput} from "@mantine/dates";
import React, {useContext, useEffect, useLayoutEffect, useState} from "react";
import {Cards} from "@/app/home/page";
import Cookies from "js-cookie";
import {LoadingContext} from "@/app/LoadingContext";


interface SearchObj {
    title: string,
    from: Date | null,
    to: Date | null,
    place: string[],
    tags: string[]
}


export default function Search(props: any) {
    const loading = useContext(LoadingContext)
    const searchObjInit: SearchObj = {
        title: "",
        from: null,
        to: null,
        place: [],
        tags: [],
    }
    const [searchObj, setSearchObj] = useState<SearchObj>(searchObjInit)
    const [listPlace, setListPlace] = useState<string[]>([])
    const [tagList, setTagList] = useState<string[]>([])
    const [isContainerVisible, setIsContainerVisible] = useState(false);

    useEffect(() => {
        console.log(searchObj)
    }, [searchObj.place, searchObj.title, searchObj.from, searchObj.to,]);

    useEffect(() => {
        if (!loading) {
            getPlace().then()
            getTag().then()
        }
    }, []);

    useLayoutEffect(() => {
        if(JSON.stringify(searchObj) === JSON.stringify(searchObjInit) && !isContainerVisible){
            props.getAllEvent();   
        }
    }, [searchObj.title, searchObj.from, searchObj.to, searchObj.place, searchObj.tags]);

    const toggleContainer = () => {
        setIsContainerVisible(!isContainerVisible);
    };

    async function getPlace() {
        const url = "http://localhost:8080/api/event/AllPlace"
        await fetch(url,
            {
                headers: {
                    "Authorization": `Bearer ${Cookies.get("token")}`
                },
            }
        ).then((v) => v.json()).then((v: string[]) => {
            console.log(v);
            setListPlace(v)
        }).catch()
    }

    async function getTag() {
        const url = "http://localhost:8080/api/event/AllTag"
        await fetch(url,
            {
                headers: {
                    "Authorization": `Bearer ${Cookies.get("token")}`
                },
            }
        ).then(
            (v) => v.json()
        ).then((v: string[]) => {
            setTagList(v)
        }).catch()
    }


    async function search() {
        const url = "http://localhost:8080/api/event/search"
        await fetch(url,
            {
                method:"POST",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${Cookies.get("token")}`
                },
                body: JSON.stringify(searchObj)
            }
        ).then((v) => v.json()).then((v: Cards[]) => {
            console.log(v);
            props.setCard(v);
        }).catch()
    }

    return (
        <Container>
            <center>
                <Button className="create" size="md" variant="filled" onClick={toggleContainer}>
                    {isContainerVisible ? "Hide Search Bars" : "Show Search Bars"}
                </Button>
                {isContainerVisible && (
                    <div style={{ display: "flex", flexDirection: "column" }}>
                        <TextInput
                            style={{ marginBottom: "8px" }}
                            label={"Title"}
                            value={searchObj?.title}
                            onChange={(e) => setSearchObj({ ...searchObj, title: e.target.value })}
                        />
                        <MultiSelect
                            value={searchObj.place}
                            onChange={(e) => setSearchObj({ ...searchObj, place: e })}
                            label={"Place"}
                            data={listPlace}
                            clearable
                        />
                        <DateInput
                            clearable
                            value={searchObj.from}
                            onChange={(e) => setSearchObj({ ...searchObj, from: e })}
                            label={"From"}
                        />
                        <DateInput
                            clearable
                            value={searchObj.to}
                            onChange={(e) => setSearchObj({ ...searchObj, to: e })}
                            label={"To"}
                        />
                        <MultiSelect
                            data={tagList}
                            onChange={(e) => setSearchObj({ ...searchObj, tags: e })}
                            value={searchObj.tags}
                            clearable
                            label={"Tags"}
                        />
                        <br />
                        <Button className="create" size="md" variant="filled" onClick={search}>
                            Search
                        </Button>
                        <Button className="create" size="md" variant="filled" onClick={() => {
                            props.getAllEvent();
                            setSearchObj(searchObjInit);
                        }}>
                            Clear
                        </Button>
                    </div>
                )}
            </center>
        </Container>
    )
}