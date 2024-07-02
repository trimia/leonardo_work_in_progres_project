export interface CardProps {
    id: number
    title: String;
    description: String;
    place: String;
    owner: String;
    datetime: string;
    image: string[];
    tags: String[] | null;
    key: number | undefined
}