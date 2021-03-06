import { FunctionalComponent, h } from 'preact';
import { Search } from 'preact-feather';

import useDebouncedCallback from 'services/debounce';

interface IProps {
    handleOnInput: (e: string) => void;
}

const SearchBar: FunctionalComponent<IProps> = (props: IProps) => {
    const debouncedSearchTerm = useDebouncedCallback((value: string) => props.handleOnInput(value), 500);

    return (
        <div class="search-bar">
            <div class="my-auto fill-current pointer-events-none text-gray-600 w-4 h-4">
                <Search size={20} />
            </div>
            <input
                type="text"
                placeholder="Search by title or description"
                class="ml-5 bg-transparent search-input"
                aria-label="Search for content by title or description"
                onInput={(e): void => debouncedSearchTerm((e.target as HTMLInputElement).value)}
            />
        </div>
    );
};

export default SearchBar;
