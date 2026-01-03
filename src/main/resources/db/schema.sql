CREATE TABLE IF NOT EXISTS category (
                                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                                        name TEXT NOT NULL UNIQUE
);

INSERT OR IGNORE INTO category(name) VALUES ('Pasta');
INSERT OR IGNORE INTO category(name) VALUES ('Meat');
INSERT OR IGNORE INTO category(name) VALUES ('Salads');
INSERT OR IGNORE INTO category(name) VALUES ('Cakes');
INSERT OR IGNORE INTO category(name) VALUES ('Soups');

CREATE TABLE IF NOT EXISTS recipe (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        name TEXT NOT NULL,
                        description TEXT,
                        difficulty TEXT NOT NULL,
                        total_duration_minutes INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS step (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      recipe_id INTEGER NOT NULL,
                      sequence_number INTEGER NOT NULL,
                      title TEXT NOT NULL,
                      description TEXT,
                      duration_minutes INTEGER NOT NULL,
                      FOREIGN KEY (recipe_id) REFERENCES recipe(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS ingredient (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            name TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS step_ingredient (
                                 step_id INTEGER NOT NULL,
                                 ingredient_id INTEGER NOT NULL,
                                 quantity REAL,
                                 unit TEXT,
                                 note TEXT,
                                 PRIMARY KEY (step_id, ingredient_id),
                                 FOREIGN KEY (step_id) REFERENCES step(id) ON DELETE CASCADE,
                                 FOREIGN KEY (ingredient_id) REFERENCES ingredient(id)
);
