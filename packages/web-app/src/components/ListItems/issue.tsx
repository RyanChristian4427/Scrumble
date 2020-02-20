import { FunctionalComponent, h } from 'preact';
import { Issue } from 'models/Issue';
import { Link } from 'preact-router';

const IssueListItem: FunctionalComponent<Issue> = (props: Issue) => {
    return (
        <div className="lst-itm-container">
            <div className="px-4 py-2">
                <Link href={`/issue/${props.id}`} class="truncate">
                    {props.name}
                </Link>
            </div>
            <div className="px-4 py-2 z-1">
                <span className="story-pnt">{props.storyPoint}</span>
            </div>
        </div>
    );
};

export default IssueListItem;
