# Code Contributions and Code Reviews

#### Focused Commits

Grade: Very Good

Feedback: The repository has a good amount of commits. 
Commits only affect a small number of files and aggregate a coherent change to the system.
Most commit messages are concise one liners, which clearly summarize the change.
The source code does not contain big pieces of commented code. 

I saw that you had a lot of "fix / update " commits. You could try using following git command:
git commit –amend.
Google what it does :)


#### Isolation

Grade: Good

Feedback: The group uses feature branches/merge requests to isolate individual features during development.
Even small changes like "removing imports", should not be done on Main. (Especially if it also fails the build) 
Always branch out!

https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-59/-/commit/780d79e712afe6985079204f8ac9c5844d90c9c0

Besides that you have a good amount of successfully integrated MRs. 

#### Reviewability

Grade: Good/Very Good

Feedback: MRs should contain a low number of formatting changes (e.g., re-organization of imports, tabs/spaces, ...). 
But sometimes that's not the case for you : https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-59/-/merge_requests/26/commits

Your MRs always have a clear focus that sometimes becomes clear from the title and the description. Some of you could further improve on that. 
Your MRs usually only cover small number of commits, which is good.
The changes are coherent and relate to each other.

#### Code Reviews

Grade: Very Good

Feedback: Code reviews are an actual discussion with a back and forth of questions and answers. 
Comments in the MRs are constructive and goal oriented.
The reviews actually lead to iterative improvements of the code as shown in this MR: 
https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-59/-/merge_requests/21

#### Build Server

Grade: Insufficient

Feedback: You should try not to merge MRs that fail the pipeline. First fix the pipeline, then merge.
https://gitlab.ewi.tudelft.nl/cse1105/2022-2023/teams/oopp-team-59/-/merge_requests/23
The pipeline should not fail on main!

You should have more than 10 checkstyle rules. 
Your builds fail often. Try checking locally if your builds pass/fail before pushing the code. 

