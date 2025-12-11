TITLE:

**TECHNICAL SPECIFICATION**

**_Name of the System_**

_The text in italics in this document is provided for guidance in preparing the specification and should be deleted from the document before issue. Text not in italics can be retained._

AUTHOR : _Author's name_ DATE : _date_

LAST REVIEW DATE : _last review date_

|     |     |
| --- | --- |
| **IT IS Architect Signoff** |     |
| Document Review by: |     |
| Document Review Date: |     |
| SSR Ticket Number &lt;xxx&gt;: |     |

**CONTENTS**

1\. Document Control 3

1.1 Change History 3

1.2 Document Cross-referenced 3

1.3 Abbreviation 3

1.4 Distribution list 3

2\. Introduction 4

2.1 System Objective 4

2.2 Scope 4

3\. Hardware, Software and Network Requirement 5

3.1 Components and Relationship 5

3.2 Hardware 5

_3.2.1_ _Name of the hardware component_ 5

_3.2.2_ _Repeat 3.2.1 for every hardware components_ 5

3.3 Software 5

_3.3.1_ _Name of the software component_ 5

_3.3.2_ _Repeat 3.3.1 for every software components_ 6

3.4 Network 6

_3.4.1_ _Name of the network component / equipment_ 6

_3.4.2_ _Repeat 3.4.1 for every network components_ 6

4\. System Design Overview 7

4.1 Build Philosophy 7

4.2 Design Limitation 7

4.3 Availability, Redundancy, and Failover 7

4.4 Security 7

_4.4.1_ _Security Zoning_ 7

_4.4.2_ _Authentication_ 8

_4.4.3_ _Authorization and Access Control_ 8

_4.4.4_ _Password Control_ 8

_4.4.5_ _Audit Trial_ 9

_4.4.6_ _Encryption of Information Storage_ 9

_4.4.7_ _Encryption of Information Transmission_ 9

_4.4.8_ _Data Integrity Checking_ 9

_4.4.9_ _Repeat for all other security requirements_ 10

5\. Database Specification 11

5.1 Database Design 11

5.2 Detail Description of File Structure / Table 11

_5.2.1_ _Name of the file / table_ 11

_5.2.2_ _Repeat 5.2.1 for all files / tables_ 12

6\. Sub-system Design 13

6.1 Overall Structure 13

6.2 Name of the sub-system 13

_6.2.1_ _Name of the module / program_ 13

_6.2.2_ _Repeat 6.2.1 for every modules / programs in the sub-system_ 14

6.3 Repeat 6.2 for every sub-systems 14

7\. Common System Features 15

A. Appendices 16

# Document Control

## Change History

|     |     |     |     |
| --- | --- | --- | --- |
| Version<br><br>Number | Issue Date | Author | Abstract |
|     |     |     |     |

## Document Cross-referenced

_Give a list of materials / documents referred by this document._

## Abbreviation

_List all abbreviations used in this document and provide their full forms._

## Distribution list

_List names of teams, units or divisions who will receive this document._

# Introduction

## System Objective

_Briefly describe the objective of the system in terms of services or application functionalities will deliver to the business to satisfy their business needs_

## Scope

_Describe the overall scope of the system. Both functional and ITSMF security requirements are clearly addressed._

# Hardware, Software and Network Requirement

_This section describes the system's hardware, software and network configurations._

## Components and Relationship

_Give an overall picture of major hardware and software components in the system. Diagrams are used to illustrate the relationships among the components._

_Components on diagrams are labelled such that they can be easily referred to._

## Hardware

### _Name of the hardware component_

_Describe the hardware component and its purpose. If there are multiple hardware components in the system, specify its roll and its relationship to other components._

_Give out the configuration details of the component, which may include:_

_\- model name / number,_

_\- CPU, memory, disk storage,_

_\- version and patch level of the OS/firmware,_

_\- OS hardening baseline,_

_\- communication interface and protocol,_

_\- special system software and utilities to be installed._

### _Repeat 3.2.1 for every hardware components_

## Software

### _Name of the software component_

_Describe the software component and its purpose in the system._

_Give out the configuration details of the component, which may include:_

- _name and version,_
- _the hardware components where the software runs on,_
- _software prerequisite, OS version, service pack version,_
- _parameter settings, e.g. memory buffer, CPU time allocated,_
- _storage location, installation directory, data directory._

### _Repeat 3.3.1 for every software components_

## Network

_If the system runs on or delivers services through network(s), a network diagram is provided in this section._

_The network diagram specifies the network location of sub-systems, modules or components; illustrates the network separation and boundary. The associated connectivity methods are identified, where details include but not limited to:_

- _Network protocol,_
- _Network port,_
- _Maximum number of connections,_
- _Maximum traffic (bandwidth)._

_Network components that are specific to the application (i.e. not shared by other systems), they are described in separate sub-sections._

### _Name of the network component / equipment_

_Describe the component and its purpose in the system._

_Give out the configuration details of the component, which may include:_

- _model name / number,_
- _version and patch level of the OS / firmware,_
- _communication interface and protocol._

_If the component is a PC / server box rather than an embedded hardware, specify:_

_\- CPU, memory, disk storage,_

_\- OS hardening baseline,_

- _software prerequisite, OS version, service pack version,_

_\- special system software and utilities to be installed._

### _Repeat 3.4.1 for every network components_

# System Design Overview

_This section describes the overall system design._

## Build Philosophy

_Describe the approach or methodology adopted in the designing the system, as well as major design decisions and their rational behind._

## Design Limitation

_State the technical limitations and constraints that have implications on the overall design. Examples are:_

_\- Dependencies from external parties,_

- _Hardware and software capability,_
- _Network bandwidth constraints._

## Availability, Redundancy, and Failover

_For Core Mission Critical system has high-availability requirement, this section should cover at least the following:_

_\- Redundancy of hardware, software and network_

- _Disaster recovery design and failover arrangement_

_Based on the availability requirements stated in Functional Specification, explain the system architecture and design adopted to fulfil the requirements._

## Security

_Based on the security requirements stated in Functional Specification, explain the underlying mechanisms adopted to fulfil the requirements._

_Define the specific security criteria by considering the elements of attack surface in order to identify mitigating controls. Describe the security controls with their positioning and relationship to the overall system design._

_Note that:_

- _Specify and document the exclusion if the system cannot fulfil any security requirements due to technical limitations._
- _Seek for proper approval (waiver) if the system cannot comply with any compliance requirements stated on IT standards_

### _Security Zoning_

_Security zoning is implemented to restrict communication by separating hosts into network zones based on risk exposure and functionality stated in Functional Specification. Explain the security zoning design, which covers how major sub-systems, modules or components are hosted in network zones and the rationale of such design._

### _Authentication_

_Explain the authentication mechanism(s), which covers:_

- _technology (e.g.. token keys / cards, PKI certificates, password etc.),_
- _procedure,_
- _common routines / library,_
- _algorithms (for computing hash, user profile encryption etc.)._

_Note that:_

- _this section covers all the authentication mechanisms for different groups of users (e.g. administrators, operators, end-users),_
- _authentication is not limited to “user logon”, incoming messages / requests from external systems are also authenticated._

### _Authorization and Access Control_

_Explain the authorization and access control mechanism(s), which covers:_

- _approach (e.g. role-based access control),_
- _technology (e.g.. application user profile, ACL),_
- _procedure (e.g. maker / checker)._

_Note that:_

- _the access control requirements for different groups of users (e.g. normal users, privileged users, support administrators and operators) can be different,_
- _authorization is not limited to human user; it also applies to system processes and threads (either internal or external)._

### _Password Control_

_Explain the password control mechanism(s), which covers:_

- _password creation,_
- _password storage,_
- _password distribution,_
- _password quality,_
- _password change,_
- _password deletion and revocation._

_Note that:_

- _Specify compensating controls if the system cannot enforce certain password controls due to system limitation._

### _Audit Trial_

_Describe the design of the auditing features and how they are implemented._

_Note that:_

- _Specify the applicability of SIEM (Security Information and Event Management) monitoring, the method and approach of integration with SIEM system, and/or the associated compensating measure if the system cannot integrate with SIEM as required in ITSMF and consult with ITC&SM as necessary._
- _At minimal, the following audit trail should be available for security monitoring:_

1.  _User authentication, both success and failure_
2.  _Login and logout_
3.  _Switching to a super user (e.g. root, administrator)_
4.  _User ID creation, deletion, and privilege change activity_
5.  _Modification of system or security setting_
6.  _Security events where the IT security measures have been started, stopped or overridden_
7.  _Modification, deletion or interruption to audit and security logs_

- _For SIEM integration, the following need to be included:_

<div class="joplin-table-wrapper"><table><tbody><tr><td><p></p></td><td><p><strong><em>Description</em></strong></p></td><td><p><strong><em>Design/Implementation</em></strong></p></td></tr><tr><td><p><em>Log format</em></p></td><td><p><em>In what format the audit trials are presented in</em></p><ul><li><em>Application</em></li><li><em>Database</em></li><li><em>System OS</em></li><li><em>Any other purchased products</em></li></ul><p></p><p><em>(e.g. comma/tab delimited, database table, OS event logs, etc.)</em></p><p></p><p><em>SIEM support Common Event Format (CEF) by default</em></p></td><td><p></p></td></tr><tr><td><p><em>Log Transfer Method</em></p></td><td><p><em>How the logs are consumed by SIEM.</em></p><p><em>(e.g. syslog, Windows event logs, text file on share folder/transferred through SFTP, database connection)</em></p></td><td><p></p></td></tr><tr><td><p><em>Log rotation settings (if any)</em></p></td><td><p><em>If the audit trials are having rotation mechanism, specific the log retention and clean-up frequency</em></p></td><td><p></p></td></tr><tr><td><p><em>Monitoring/Alerting requirements on security events</em></p></td><td><p><em>Any special security events requires SOC to monitor and alert</em></p><p></p></td><td><p></p></td></tr><tr><td><p><em>Estimate log size/event rate</em></p></td><td><p><em>A high level estimation on the estimated log size (per day) and the event rate (per second)</em></p></td><td><p></p></td></tr></tbody></table></div>

### _Encryption of Information Storage_

_Explain the encryption control(s) of information storage, which covers:_

- _targets of encryption (e.g. confidential information, password),_
- _approach (e.g. whole database encryption, field-by-field encryption),_
- _technology (e.g. off-the-shelf third-party software, custom-built program),_
- _procedure (e.g. key management),_
- _common routines / library,_
- _algorithms and key length (e.g. AES 256).._

### _Encryption of Information Transmission_

_Explain the encryption control(s) of information transmission, which covers:_

- _communication channels to be encrypted (e.g. outside data centre),_
- _approach (e.g. end-to-end encryption, network tunnelling),_
- _protocol used (e.g. TLS, SSH),_
- _technology (e.g. PKI, shared-key),_
- _procedure (e.g. key management),_
- _common routines / library,_
- _algorithms and key length (e.g. AES 256)._

### _Data Integrity Checking_

_Explain the data integrity checking mechanism(s), which covers:_

- _technology (e.g. hash, checksum),_
- _procedure,_
- _common routines / library,_
- _algorithms._

### _Repeat for all other security requirements_

_Describe the design of this feature and how it is implemented._

# Database Specification

_This section provides a detail specification on how the data is organized in the system._

## Database Design

_This section is to specify how the database is designed and give guidelines for future modification, such as:_

- _Naming convention,_
- _Storage allocation,_
- _Database organisation,_
- _Information required constructing files / tables / dictionaries etc._

## Detail Description of File Structure / Table

_This section gives detail descriptions of files and database tables in the system._

### _Name of the file / table_

_Give a brief description of the file. Explain its purpose, content and relationship to other files / tables._

_Provide a detail description, which includes:_

- _name of the file / table,_
- _keys and indexes,_
- _file type (e.g. sequential, entry sequence, relational etc.),_
- _field specification_
    - - - _field name_
            - _description / definition_
            - _format (i.e. type, size, encoding)_
            - _validation rules_

_For every indexes / catalogues associated with this file / table, specify their:_

- _index / catalogue name,_
- _base file / table name,_
- _purpose and usage,_
- _indexed fields,_
- _indexed sequence._

_Also provide environmental specific information, such as_

- _physical location (also the logical assignment if applicable),_
- _File owner,_
- _Access rights,_
- _backup and archive requirement,_
- _File size and estimated growth._

_Any additional platform specific information can also be included to facilitate future maintenance._

### _Repeat 5.2.1 for all files / tables_

# Sub-system Design

_This section describes all system functions in full detail._

_For easy understanding, the system is broken down into sub-systems and then to modules. The way to divide a system into sub-systems and the level of breakdown depend on the system's architecture, its size and complexity. For example, a simple application can be decomposed into few modules, while a complex system can be viewed as a number of sub-systems and each composed by a set of modules._

## Overall Structure

_Outline the overall structure of the system._

_Use diagrams to illustration the relationships among sub-systems._

## Name of the sub-system

_Give a detail description of the sub-system. Use diagrams, such as Data Flow Diagram (expanded from context diagram in Functional Specification) to explain data flows associated with the sub-system._

_Information includes:_

- _sub-system name, including full name and id (if any),_
- _function of the sub-system,_
- _inputs from external or other sub-systems,_
- _outputs to external or other sub-systems,_
- _directory structure or programs location._

### _Name of the module / program_

_Give a detail description of the module / program._

_Information includes:_

- _program name / id (i.e. module id, screen id, or batch id),_
- _purpose,_
- _dependencies with other modules (both compile time and run time),_
- _start up and termination conditions,_
- _input and output_
    - - - _files_
            - _parameters_
            - _messages_
            - _terminals / devices / systems_
- _processing mode (e.g. online, batch, real-time),_
- _execution frequency (e.g. daily, weekly, monthly, periodic, on request),_
- _interface,_
- _processing logic\*,_
- _remarks (i.e. additional information facilitating on-going maintenance)._

_\* Processing logic is required for modules that are critical or highly complex. If presented, it is in the form of pseudo codes._

### _Repeat 6.2.1 for every modules / programs in the sub-system_

## Repeat 6.2 for every sub-systems

# Common System Features

_Provide information and explain the usage of common system features that are shared across sub-systems or modules, such as:_

_Common module / routine / framework_

_Describe library modules, common routines that shared by sub-systems / modules. For example:_

- _copy library,_
- _calculation routine (e.g. checksum calculation),_
- _hash computation, encryption routine (e.g. SHA-2, AES)._

_Exception handling_

_Describe common exception handling routine / approach that is common to the sub-systems / modules. For example: error report and logging._

_Design and programming guidelines_

_Describe or provide reference to any system specific design or programming guideline that is adopted by this system. For example:_

- _user interface design guide;_
- _coding standard or programming style._

_Also specify if there is any design or programming rules and restrictions that must be followed in design new modules or making enhancements. For example:_

- _buffers must be in the same size for processes that communicate with each other,_
- _use triggers instead of polling in database design for real-time modules._

# Appendices

_Additional information can be included to the document as appendix. To facilitate future maintenance, frequently updated materials are put into appendices. The materials to be put into appendices are system dependent. The following are some examples,_

_System and error message_

_Give a list of system and error messages. Provide their code, text, and explanation of possible causes._

_Batch jobs or processes schedule_

_List all batch jobs and their processing schedule._

_Database construction script_

_List script files for creating database tables._

_Build procedure and script_

_Provide build procedure information or list script files for building the system._

_Disk space allocation and estimation_

_Provide disk space allocation information, including the estimation of future growth._