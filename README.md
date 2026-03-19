# issue-git-cli

A lightweight, cross-platform Command Line Interface (CLI) application built in Kotlin. This tool directly interacts with the GitHub API to fetch, parse, and display active repository issues directly in your terminal, making it perfect for quick team standups and sprint tracking.

## Prerequisites
- java 17 (or higher)
- Github PAT: A Personal Access Token. For instructions on how to generate one, see [Github Official guide](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens)

## Installation

1. Download the latest `.zip` file from the [release page](https://github.com/GibranMaulana/issue-git-cli/releases) and extract it to a permanent folder on your machine.
2. in the `bin` folder, you can rename or copy `.env.example` file to `.env`
	- linux / macOS
		```bash 
		cp .env.example .env
		```
	- windows (command prompt)
		```DOS
		copy .env.example .env
		```
3. Open the new `.env` file in any text editor and paste your GitHub token:
	```Code Snippet
	GITHUB_TOKEN=gph_your_token_here
	```

4. add `bin` directory to your PATH
	- linux

		```bash
		#zsh
		echo 'export PATH="$PATH:/your/new/path"' >> ~/.zshrc

		source ~/.zshrc
		```
		or
		```bash
		#bash
		echo 'export PATH="$PATH:/your/new/path"' >> ~/.bashrc

		source ~/.bashrc
		```
	- windows

		Paste the absolute, full folder path of your extracted `bin` to `path` environment Variables

## Usage
You can run the command in anywhere
```bash
issue-git-cli
```

## Development Setup
To Build and Modify the tool locally:

1. clone the repository
	```bash
	git clone https://github.com/GibranMaulana/issue-git-cli.git

	cd issue-git-cli
	```
2. set up your environment
	```bash
	#in app/ folder
	echo "GITHUB_TOKEN=your_token_here" > .env
	```
3. Build and Run via Gradle
	```bash
	./gradlew build && ./gradle run
	```

4. Compile Executables
	```bash
	./gradle installDist
	```