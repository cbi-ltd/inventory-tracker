## Project Overview

This application will be herein reffered to as Fuel-flow or Fuel-station application or fuel-station-app OR Fuel-station-application. It is a sort of OPERATIONS MANAGEMENT & REPORTING APPLICATION FOR OIL COMPANIES THAT OWN FUEL STATIONS

This application will be for Oil Companies with fuel stations.
The ultimate aim is to create a comprehensive report of all the Company's filling-station's business activities for a specified time period


The organization this is being built for already has a working application (called ``CAMS``) that processes payments via POS-terminals (card & transfers)
This fuel-station-app will eventually be integrating with CAMS for POS-terminal payments
Fuel-station application relies on ``CAMS`` for authentication and also for payment

``CAMS`` classifies businesses into: "Aggregator", "Institution", "Merchants", "Agents", "Outlets", "Staff".
An institution usually has merchants under them, though there are merchants that are not under an institution.  Institutions can create outlets, outlets can have staffs. Agents can do more with POS terminals (inclusive of Value Added Sevices) than Merchants as Merchants simply collect payments into their bank accounts,  POS-Terminals are mapped to Agents, Merchants, Outlets

Every of these classifications on ``CAMS`` mean very little to this Fuel-flow application, as Fuel-flow is simply concerned with: "What Company does these filling stations belong to" (so that's just _Company and Filling Station_)

Fuel-station-application tracks daily sales, inventory, transactions of each filling station that belongs to a particular Company. 
A filing station can have multiple pumps that dispense fuel (petrol, diesel, kerosene, gas).
Each pump will be assigned to an attendant who does the selling/dispensing. The attendants usually starts their day by noting the initial pump-reading and at the end of the day also noting the closing pump-reading, therefore knowing the quantity sold for that day.
Filing stations usually also sell some other products.

A Company can have many Stations.
A station can have several pumps, several staff-members and several attendants, attendants are also staff-members.
There is also an admin staff.
Pumps are assigned daily to an attendant (that means the attendant on a pump today might not be the attendant on same pump tommorrow)

Each pump will also be assigned to a POS-terminal via the POS terminal's serial number (if possible, provision can be made for attendants briefly lending another attendant's POS terminal for certain transactions - due to network issues or any other issue - this might violate report authenticity as we aim to know what attendant manned this POS terminal and is therefore responsible for ALL the transactions made via this POS terminal)

Attendants clock-into their pumps at a set-time with an initial pump-reading and clock-out of it at a set-time with a closing reading

Note that i already have a seperate model for pump-audits, because the vision of this fuel-application is to eventually generate a very comprehensive report that top-executives of the Companies can look at to see business performance and monitor inflow, outflow and general transactional activities

Most payments will be by transfer and card via the POS-terminals, which the ``CAMS`` application already handles very well

A very important part of this fuel-application will be the functionalites that call CAMS for payment and authentication

I need the application to somehow make room for cash payments and how they will also be accounted for in the eventual report (because some customers will actually pay via cash)

On a sidenote, ``CAMS`` was built as a microservice architecture, having more tha 8 microservices (Request Processor, Switch Gateway, Users/Terminals/Wallet management, VAS, Transactions, Reporting)
The microservices communicate via Kafka events and the whole architecture of ``CAMS`` sits in a kubernetes network

## Simplified Functional Requirements (Operations)

• System Relies On CAMS For Authentication Via Tokens
• Create Station
Each station shall have:  Name, Address, Pumps, Products, Staff, Inventory
• Create Staff
	Admin, Attendant
• Create Pump
Each pump shall have:  Pump Number, Fuel Product, POS Terminal, Assigned 
Attendant
• Product Type
	Admin can add product, remove product, set selling price,
update selling price
• Non-Fuel Products are supported 
Full Station Inventory and Sale Tracking
• Admin Records Inventory
• Admin Assigns Attendants To Pumps
• Attendant Logs Opening Meter Reading, System Validates
• System Receives And Stores Payment Details
• System Computes Total Litres Sold For The Day
• System Manages Inventory And Computes Consumption
	Opening stock, Supply, Transfers, Sales, Closing stock
	Losses, Adjustments
