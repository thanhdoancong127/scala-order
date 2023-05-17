
-- !Ups
create table scalaorder.users (
  id serial constraint users_pk primary key,
  email varchar(255) not null,
  first_name varchar(255) not null,
  last_name varchar(255) not null,
  password varchar(255) not null,
  role varchar(255) not null,
  birthday date,
  address varchar(255),
  phone_number varchar(255)
);

create table scalaorder.orders (
  id serial constraint order_pk primary key,
  user_id int not null,
  order_date date not null,
  total_price decimal(10,2) not null
);

create table scalaorder.products (
  id serial constraint product_pk primary key,
  product_name varchar(255) not null,
  price decimal(10,2) not null,
  exp_date date not null
);

create table scalaorder.order_details (
  id serial constraint orderdetails_pk primary key,
  order_id int not null,
  product_id int not null,
  quantity int not null,
  price decimal(10,2) not null
);

-- !Downs

DROP TABLE scalaorder.users;
DROP TABLE scalaorder.orders;
DROP TABLE scalaorder.order_detail;
DROP TABLE scalaorder.products;
DROP TABLE public.play_evolutions;
