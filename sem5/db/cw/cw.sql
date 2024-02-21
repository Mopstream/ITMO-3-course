create table crops (
    crop_id serial primary key, 
    name text not null,
    humidity real check (humidity >= 0 and humidity <= 100),
    brightness real check (brightness > 0),
    is_legal boolean not null,
    brain_damage real check (brain_damage > -200 and brain_damage < 200)
);


create table estate (
    estate_id serial primary key, 
    name text not null,
    area real not null check (area > 100),
    county text not null,
    rent real not null check (rent > 0)
);


create table plantation (
    plantation_id serial primary key,
    capacity integer not null,
    revenue real not null,
    estate_id integer references estate(estate_id)
);


create table crops_plantations (
    crop_id integer references crops(crop_id),
    plantation_id integer references plantation(plantation_id),
    counter integer check (counter >= 0),
    primary key(crop_id, plantation_id)
);


create table worker (
    worker_id serial primary key,
    plantation_id integer references plantation(plantation_id),
    name text not null,
    salary real check (salary >= 0),
    profession text not null
);


create table worker_family_member (
    worker_fm_id serial primary key,
    worker_id integer references worker(worker_id),
    name text not null,
    relationship text not null,
    adress text not null
);


create table landlord (
    landlord_id serial primary key,
    estate_id integer references estate(estate_id),
    name text not null,
    capital real check (capital > 0)
);


create table evidence_info (
    evidence_info_id serial primary key,
    landlord_id integer references landlord(landlord_id),
    description text,
    worth real check (worth >= 20 AND worth <= 100)
);


create table manager (
    manager_id serial primary key,
    plantation_id integer references plantation(plantation_id),
    name text not null,
    salary real check (salary >=100)
);


create table client (
    client_id serial primary key,
    manager_id integer references manager(manager_id),
    name text not null,
    debt real check (debt < 10000),
    is_highly_addicted boolean,
    brain_resource real
);


create table client_family_member(
    client_fm serial primary key,
    client_id integer references client(client_id),
    adress text not null,
    email text unique check (email ~ '^[a-zA-Z0-9.!#$%&''*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$')
);


create or replace function  cell(id_client integer, id_crop integer, amount integer)
returns void as $$
    begin
        update crops_plantations set counter = counter - amount where crop_id = id_crop and counter >= amount;
        update client set brain_resource = brain_resource - (select brain_damage from crops where crop_id = id_crop) where client_id = id_client;
    end;
$$ language plpgsql;




create or replace function killer_f()
returns trigger as $$
    begin
        if (NEW.brain_resource <= 0)
        then begin
                delete from client_family_member where client_id = NEW.client_id;
                delete from client where client_id = NEW.client_id;
                return null;
            end;
        else return NEW;
    end;
$$ language plpgsql;


create trigger killer_t
before insert or update on client
for each row execute function killer_f();



create or replace function plant_capacity_checker_f()
returns trigger as $$
    begin
        if (select sum(counter) from crops_plantations where plantation_id = NEW.plantation_id) 
           + 
           (NEW.counter - OLD.counter)
           > (select capacity from  plantation where plantation_id = NEW.plantation_id)
       then 
            begin
                raise notice 'please add new plantation';
                return null;
            end; 
        else return NEW; 
       end if;
    end;
$$ language plpgsql;


create trigger plant_capacity_checker_t
before insert or update on crops_plantations
for each row execute function plant_capacity_checker_f();


create or replace function enum_crops_on_plantation(name text)
returns setof text as $$
    begin
        select name from crops c
        join crops_plantations cp on c.crops_id = cp.crops_id
        join plantation p on cp.plantation_td = p.plantation_id
        where p.name = name;
    end;
$$ language plpgsql;


create or replace function get_friends_email(name text)
returns setof text as $$
    begin
        select email from client_family_member
        where client_id = (select client_id from client where client.name = name);
    end;
$$ language plpgsql;

create index crop_id_i on crops using hash(crop_id);
create index client_id_i on client using hash(client_id);
create index brain_damage_i on crops using btree(brain_damage);

