-- create tables (very basic version of twitter)

CREATE MEMORY TABLE users (
    user_id INTEGER IDENTITY PRIMARY KEY,
    username VARCHAR(100),
    email VARCHAR(100),
    phone DECIMAL(10)
);

CREATE MEMORY TABLE followers (
    user INTEGER,
    follower INTEGER,
    CONSTRAINT FOREIGN KEY (user, follower) REFERENCES users user_id
);

CREATE MEMORY TABLE tweets (
    tweet_id INTEGER IDENTITY PRIMARY KEY,
    user INTEGER,
    content VARCHAR(280),
    time TIMESTAMP(2) DEFAULT CURRENT_TIMESTAMP(2),
    CONSTRAINT FOREIGN KEY user REFERENCES users user_id
);

-- add data for testing

INSERT INTO users (name, email, phone) VALUES ('wil_gaboury', 'wil.gaboury@exmaple.com', 7185551234);
INSERT INTO users (name, email, phone) VALUES ('john_smith', 'john.smith@exmaple.com', 718555-2345);
INSERT INTO users (name, email, phone) VALUES ('jane_doe', 'jane.doe@exmaple.com', 7185553456);
INSERT INTO users (name, email, phone) VALUES ('jennifer_lawrence', 'jenn.lawrence@exmaple.com', 7185554567);
INSERT INTO users (name, email, phone) VALUES ('donald_trump', 'donald.trump@exmaple.com', 7185555678);

INSERT INTO followers (user, follower) VALUES ('wil_gaboury', 'john_smith');
INSERT INTO followers (user, follower) VALUES ('john_smith', 'wil_gaboury');
INSERT INTO followers (user, follower) VALUES ('john_smith', 'jane_doe');
INSERT INTO followers (user, follower) VALUES ('jennifer_lawrence', 'wil_gaboury');
INSERT INTO followers (user, follower) VALUES ('jennifer_lawrence', 'john_smith');
INSERT INTO followers (user, follower) VALUES ('jennifer_lawrence', 'jane_doe');
INSERT INTO followers (user, follower) VALUES ('donald_trump', 'wil_gaboury');
INSERT INTO followers (user, follower) VALUES ('donald_trump', 'john_smith');
INSERT INTO followers (user, follower) VALUES ('donald_trump', 'jane_doe');
INSERT INTO followers (user, follower) VALUES ('donald_trump', 'jennifer_lawrence');

INSERT INTO tweets (user, content) VALUES ('wil_gaboury', 'currently working on a cool library called JugSQL');
INSERT INTO tweets (user, content) VALUES ('jennifer_lawrence', 'eating some really good food, check out this pic');
INSERT INTO tweets (user, content) VALUES ('jennifer_lawrence', 'just got off the set a new x-men movie');
INSERT INTO tweets (user, content) VALUES ('jennifer_lawrence', 'go buy some of my merch or somthing');
INSERT INTO tweets (user, content) VALUES ('donald_trump', 'china is being really annoying');
INSERT INTO tweets (user, content) VALUES ('donald_trump', 'cnn is putting out fake news');
INSERT INTO tweets (user, content) VALUES ('donald_trump', 'I should really get off twitter');
INSERT INTO tweets (user, content) VALUES ('donald_trump', 'democrats are being mean to me');
