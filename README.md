<div id="top"></div>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->

<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]

<!--
[![MIT License][license-shield]][license-url]
[![LinkedIn][linkedin-shield]][linkedin-url]
-->


<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/RPTU-EIS/RISCV-Core">
    <img src="fig/RPTU_logo.png" alt="Logo" width="400" height="200">
  </a>
  <h3 align="center">
  5-Stage Pipelined RISC-V Core in Chisel
  <br />
  Educational Project
  </h3>

  <p align="center">
    <br />
    <a href="https://github.com/RPTU-EIS/RISCV-Core/issues">Report Bug</a> 
  </p>
</div>

 ![CI Pipeline](https://github.com/RPTU-EIS/RISCV-Core/actions/workflows/scala.yml/badge.svg?event=push)

<br />

<!-- TABLE OF CONTENTS 
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details> -->


## Core Design

<img src="fig/RISC-V_Core_Pipeline.png" alt="Pipelines RISC-V Core Design" width="2000">

## Dependencies

The project requires the following dependencies to be installed:

- Scala 2.12.13
- Chisel 3.5

## Usage

To run the project, simply run the following command in your terminal:

`sbt run`


This will generate Verilog code for the pipelined processor.

To run the testbench, simply run the following command in your terminal:

`sbt test`

This will simulate it using the included testbench.

Note that testHarness variables are only for testing purposes and are not necessary for the project structure.

## Tools

GTKWave can be used for debugging or to verify the results.

## Structure

The project is structured as follows:

- `RISCV_TOP.scala`: The main Chisel file containing the pipelined RISC-V processor.
- `RISC_TOP_tb.scala`: A ScalaTest spec that tests the pipelined processor.

## Participants and Collaborators

Supervision and Organization: Tobias Jauch, Philipp Schmitz, Alex Wezel

Student Workers: Giorgi Solomnishvili, Zahra Jenab Mahabadi, Tsotne Karchava, Abdullah Shaaban Saad Allam.

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/TUK-EIS/ADSProject.svg?style=for-the-badge
[contributors-url]: https://github.com/TUK-EIS/ADSProject/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/TUK-EIS/ADSProject.svg?style=for-the-badge
[forks-url]: https://github.com/TUK-EIS/ADSProject/network/members
[stars-shield]: https://img.shields.io/github/stars/TUK-EIS/ADSProject.svg?style=for-the-badge
[stars-url]: https://github.com/TUK-EIS/ADSProject/stargazers
[issues-shield]: https://img.shields.io/github/issues/TUK-EIS/ADSProject.svg?style=for-the-badge
[issues-url]: https://github.com/TUK-EIS/ADSProject/issues
<!--
[license-shield]: https://img.shields.io/github/license/othneildrew/Best-README-Template.svg?style=for-the-badge
[license-url]: https://github.com/othneildrew/Best-README-Template/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/othneildrew
-->
[product-screenshot]: images/screenshot.png
