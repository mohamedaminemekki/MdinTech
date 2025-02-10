CREATE TABLE users (
                       CIN INT PRIMARY KEY,  -- Unique identification number for the user
                       Name VARCHAR(100) NOT NULL,  -- User's full name
                       Email VARCHAR(255) UNIQUE NOT NULL,  -- Email should be unique
                       Password VARCHAR(255) NOT NULL,  -- Hashed password (BCrypt)
                       Role ENUM('ADMIN', 'USER', 'TRAINER', 'MANAGER') NOT NULL,  -- User roles
                       Phone VARCHAR(20),  -- User's phone number
                       Address VARCHAR(255),  -- Address field
                       City VARCHAR(100),  -- City of residence
                       State VARCHAR(100),  -- State of residence
                       Status BOOLEAN DEFAULT TRUE  -- User status (active/inactive)
);
CREATE TABLE parking (
                         ID INT AUTO_INCREMENT PRIMARY KEY,   -- Unique identifier for each parking
                         Name VARCHAR(255) NOT NULL,          -- Name of the parking lot
                         Localisation VARCHAR(255) NOT NULL,  -- Location details
                         Capacity INT NOT NULL                -- Number of parking slots available
);
CREATE TABLE parking_slot (
                              SlotID INT AUTO_INCREMENT PRIMARY KEY,   -- Unique identifier for parking slot
                              ParkingID INT NOT NULL,                  -- Foreign key referencing parking
                              SlotName VARCHAR(50) NOT NULL,           -- Name of the slot (e.g., A1, B2)
                              Available BOOLEAN DEFAULT TRUE,          -- Slot availability (true/false)
                              FOREIGN KEY (ParkingID) REFERENCES parking(ID) ON DELETE CASCADE
);
CREATE TABLE parking_ticket (
                                TicketID INT AUTO_INCREMENT PRIMARY KEY, -- Unique identifier for ticket
                                User_ID INT NOT NULL,                    -- Foreign key referencing users
                                Parking_ID INT NOT NULL,                  -- Foreign key referencing parking
                                Parking_Slot_ID INT NOT NULL,             -- Foreign key referencing slot
                                Issuing_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Time of issue
                                Expiration_Date TIMESTAMP NOT NULL,       -- Expiry time
                                Status BOOLEAN DEFAULT TRUE,              -- Ticket status (active/inactive)
                                FOREIGN KEY (User_ID) REFERENCES users(CIN) ON DELETE CASCADE,
                                FOREIGN KEY (Parking_ID) REFERENCES parking(ID) ON DELETE CASCADE,
                                FOREIGN KEY (Parking_Slot_ID) REFERENCES parking_slot(SlotID) ON DELETE CASCADE
);
