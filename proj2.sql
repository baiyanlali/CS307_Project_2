create table course(
    course_id varchar primary key,
    course_name varchar not null ,
    credit int not null ,
    course_hour int not null,
    pre_pattern varchar
);

create table semester(
    sem_id serial primary key ,
    sem_name varchar not null ,
    sem_begin date not null ,
    sem_end date not null
);

create table section(
    course_id varchar not null ,
    sec_id serial primary key ,
    semester_id int not null ,
    sec_name varchar not null ,
    tot_capacity int not null ,
    left_capacity int not null,
    foreign key (course_id) references course(course_id),
    foreign key (semester_id) references semester(sem_id)
);

/*note: use numbers to represent days*/
create table class(
    sec_id int not null ,
    class_id serial primary key ,
    day_of_week int check ( day_of_week between 1 and 7) not null ,
    week_list varchar not null ,
    class_begin int not null ,
    class_end int not null ,
    foreign key (sec_id) references section(sec_id)
);

create table location(
    class_id int not null ,
    loc varchar not null ,
    foreign key (class_id) references class(class_id)
);


/*classified_as mean classified as teacher or student*/
create table users(
    id int primary key ,
    first_name varchar not null ,
    last_name varchar not null,
    classified_as int not null
);

create table student_info(
    sid int not null unique ,
    major_id int,
    enroll_date date not null,
    foreign key (sid) references users(id)
);

create table teaching_info(
    instructor_id int not null ,
    class_id int not null ,
    foreign key (instructor_id) references users(id),
    foreign key (class_id) references class(class_id)
);


/*name unique!!!*/
create table department(
    dept_id serial primary key ,
    dept_name varchar not null unique
);


create table major(
    dept_id int not null ,
    major_id serial primary key ,
    major_name varchar not null,
    foreign key (dept_id) references department(dept_id)
);

create table major_course(
    major_id int not null ,
    course_id varchar not null ,
    type int not null ,
    foreign key (major_id) references major(major_id),
    foreign key (course_id) references course(course_id)
);


/*grades is in 100, grading type is P/F*/
create table learning_info(
    sid int not null ,
    course_id varchar not null ,
    grades int check ( grades between 0 and 100),
    grading_type varchar(1),
    foreign key (sid) references users(id),
    foreign key (course_id) references course(course_id)
);

/*order by index, calrify the order of list courses*/
create table pre_courses(
    index serial primary key,
    course_id varchar not null ,
    pre_course_id varchar not null ,
    foreign key (course_id) references course(course_id),
    foreign key (pre_course_id) references course(course_id)
);





create or replace function add_department(dept_nam varchar)
    returns integer
as $$
begin
    insert into department(dept_name) values(dept_name);
    return lastval();
end;

$$
    language plpgsql;


create or replace function add_semster(sem_nam varchar,begin date,endd date)
    returns integer
as $$
begin
    insert into semester(sem_name, sem_begin, sem_end) VALUES (sem_nam,begin,endd);
    return lastval();
end;

$$
    language plpgsql;



create or replace function remove_semester(id integer) returns void
as $$
begin
    delete from learning_info where sid in
                                    (
                                        select sid from learning_info where sec_id in
                                                                            (select sec_id from section where semester_id=id)
                                    );

    delete from teaching_info where class_id in (
        select class_id from class where sec_id in
                                         (select sec_id from section where semester_id = id)
    );

    delete from class where sec_id in (select sec_id from section where semester_id = id);

    delete from section where semester_id=id;

    delete from semester where sem_id=id;

end;
$$
    language plpgsql;


create or replace function remove_user(id integer) returns void
as $$
begin

    delete from teaching_info where instructor_id=id;

    delete from learning_info where sid=id;

    delete from student_info where sid=id;

end;
$$
    language plpgsql;




