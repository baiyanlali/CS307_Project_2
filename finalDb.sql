create table course
(
    course_id   varchar not null
        constraint course_pkey
            primary key,
    course_name varchar not null,
    credit      integer not null,
    course_hour integer not null,
    pre_pattern varchar,
    grade_type  integer
);

alter table course
    owner to postgres;

create table semester
(
    sem_id    serial  not null
        constraint semester_pkey
            primary key,
    sem_name  varchar not null,
    sem_begin date    not null,
    sem_end   date    not null
);

alter table semester
    owner to postgres;

create table section
(
    course_id     varchar not null
        constraint section_course_id_fkey
            references course,
    sec_id        serial  not null
        constraint section_pkey
            primary key,
    semester_id   integer not null
        constraint section_semester_id_fkey
            references semester,
    sec_name      varchar not null,
    tot_capacity  integer not null,
    left_capacity integer not null
        constraint section_left_capacity_check
            check (left_capacity >= 0)
);

alter table section
    owner to postgres;

create table location
(
    location_id serial  not null
        constraint location_pkey
            primary key,
    loc         varchar not null
);

alter table location
    owner to postgres;

create table class
(
    sec_id      integer not null
        constraint class_sec_id_fkey
            references section,
    class_id    serial  not null
        constraint class_pkey
            primary key,
    day_of_week integer not null
        constraint class_day_of_week_check
            check ((day_of_week >= 1) AND (day_of_week <= 7)),
    week_list   varchar not null,
    class_begin integer not null,
    class_end   integer not null,
    loc_id      integer not null
        constraint class_loc_id_fkey
            references location
);

alter table class
    owner to postgres;

create table users
(
    id            integer not null
        constraint users_pkey
            primary key,
    first_name    varchar not null,
    last_name     varchar not null,
    classified_as integer not null
);

alter table users
    owner to postgres;

create table teaching_info
(
    instructor_id integer not null
        constraint teaching_info_instructor_id_fkey
            references users,
    class_id      integer not null
        constraint teaching_info_class_id_fkey
            references class
);

alter table teaching_info
    owner to postgres;

create table department
(
    dept_id   serial  not null
        constraint department_pkey
            primary key,
    dept_name varchar not null
        constraint department_dept_name_key
            unique
);

alter table department
    owner to postgres;

create table major
(
    dept_id    integer not null
        constraint major_dept_id_fkey
            references department,
    major_id   serial  not null
        constraint major_pkey
            primary key,
    major_name varchar not null
        constraint unique_name
            unique
);

alter table major
    owner to postgres;

create table major_course
(
    major_id  integer not null
        constraint major_course_major_id_fkey
            references major,
    course_id varchar not null
        constraint major_course_course_id_fkey
            references course,
    type      integer not null
);

alter table major_course
    owner to postgres;

create table pre_courses
(
    index         serial  not null
        constraint pre_courses_pkey
            primary key,
    course_id     varchar not null
        constraint pre_courses_course_id_fkey
            references course,
    pre_course_id varchar not null
        constraint pre_courses_pre_course_id_fkey
            references course
);

alter table pre_courses
    owner to postgres;

create table student_info
(
    sid         integer not null
        constraint student_info_sid_key
            unique
        constraint student_info_sid_fkey
            references users,
    major_id    integer
        constraint student_info_major_id_fkey
            references major,
    enroll_date date    not null
);

alter table student_info
    owner to postgres;

create table learning_info
(
    sid          integer not null
        constraint learning_info_sid_fkey
            references users,
    sec_id       integer not null
        constraint learning_info_sec_id_fkey
            references section,
    grades       integer
        constraint learning_info_grades_check
            check ((grades >= 0) AND (grades <= 100)),
    grading_type varchar(1)
        constraint learning_info_grading_type_check
            check ((grading_type)::text = ANY (ARRAY [('p'::character varying)::text, ('f'::character varying)::text])),
    constraint unique_learning_info
        unique (sid, sec_id, grades, grading_type)
);

alter table learning_info
    owner to postgres;

create index index_on_learning_info
    on learning_info (sid, sec_id);

create procedure add_course(c_id character varying, c_name character varying, cred integer, hour integer, type integer,
                            pattern character varying)
    language plpgsql
as
$$
begin
    insert into course(course_id, course_name, credit, course_hour, pre_pattern, grade_type)
    VALUES (c_id,
            c_name, cred, hour, pattern, type);
end;
$$;

alter procedure add_course(varchar, varchar, integer, integer, integer, varchar) owner to postgres;

create function add_section(c_id character varying, sem integer, name character varying, cap integer) returns integer
    language plpgsql
as
$$
begin
    insert into section(course_id, semester_id, sec_name, tot_capacity, left_capacity)
    VALUES (c_id, sem, name,
            cap, cap);
    return lastval();
end;
$$;

alter function add_section(varchar, integer, varchar, integer) owner to postgres;

create function add_class(sec_idr integer, inst_id integer, week_day integer, week_listr character varying,
                          c_start integer, c_end integer, loc_in character varying) returns integer
    language plpgsql
as
$$
declare
    locid int;
begin
    if not exists(select null from location where loc = loc_in)
    then
        insert into location(loc) values (loc_in);
    end if;

    select location_id from location where loc = loc_in into locid;

    insert into class(sec_id, day_of_week, week_list, class_begin, class_end, loc_id)
    VALUES ( sec_idr, week_day
           , week_listr, c_start, c_end, locid);
    insert into teaching_info(instructor_id, class_id) VALUES (inst_id, lastval());
    return lastval();
end;
$$;

alter function add_class(integer, integer, integer, varchar, integer, integer, varchar) owner to postgres;

create procedure add_pre_course(host_id character varying, pre_id character varying)
    language plpgsql
as
$$
begin
    insert into pre_courses(course_id, pre_course_id) VALUES (host_id, pre_id);
end;
$$;

alter procedure add_pre_course(varchar, varchar) owner to postgres;

create function add_department(dept_nam character varying) returns integer
    language plpgsql
as
$$
begin
    if ((select (select dept_id from department where dept_name = dept_nam) is null) = true)
    then
        insert into department(dept_name) values (dept_nam);
        return lastval();
    else
        return -1;
    end if;
end

$$;

alter function add_department(varchar) owner to postgres;

create function add_major(m_name character varying, d_id integer) returns integer
    language plpgsql
as
$$
begin
    insert into major(dept_id, major_name) VALUES (d_id, m_name);
    return lastval();
end;
$$;

alter function add_major(varchar, integer) owner to postgres;

create function add_semster(sem_nam character varying, begin date, endd date) returns integer
    language plpgsql
as
$$
begin
    insert into semester(sem_name, sem_begin, sem_end) VALUES (sem_nam, begin, endd);
    return lastval();
end;

$$;

alter function add_semster(varchar, date, date) owner to postgres;

create function remove_major(m_id integer) returns void
    language plpgsql
as
$$
declare
    affected_sid int[];
begin
    create temporary table h1 on commit drop as (select sid
                                                 from student_info
                                                 where major_id = m_id);

    delete from student_info where major_id = m_id;
    delete from major_course where major_id = m_id;
    delete from major where major_id = m_id;

    delete from users where id in (select * from h1); --TODO:check this
end;
$$;

alter function remove_major(integer) owner to postgres;

create function add_major_course(m_id integer, c_id character varying, type_in integer) returns void
    language plpgsql
as
$$
begin
    insert into major_course(major_id, course_id, type) VALUES (m_id, c_id, type_in);
end;
$$;

alter function add_major_course(integer, varchar, integer) owner to postgres;

create function remove_semester(id integer) returns void
    language plpgsql
as
$$
begin
    delete
    from learning_info
    where sid in
          (
              select sid
              from learning_info
              where sec_id in
                    (select sec_id from section where semester_id = id)
          );

    delete
    from teaching_info
    where class_id in (
        select class_id
        from class
        where sec_id in
              (select sec_id from section where semester_id = id)
    );

    delete from class where sec_id in (select sec_id from section where semester_id = id);

    delete from section where semester_id = id;

    delete from semester where sem_id = id;

end;
$$;

alter function remove_semester(integer) owner to postgres;

create procedure addstudent(userid integer, majorid integer, firstname character varying, lastname character varying,
                            enrolleddate date)
    language plpgsql
as
$$
BEGIN
    insert into users(id, first_name, last_name, classified_as)
    values (userId, firstName, lastName, 0);
    insert into student_info(sid, major_id, enroll_date)
    values (userId, majorId, enrolledDate);
end
$$;

alter procedure addstudent(integer, integer, varchar, varchar, date) owner to postgres;

create function course_found(sectionid integer) returns boolean
    language plpgsql
as
$$
declare
    judge bool;
BEGIN
    if (select s.sec_id
        from section s
        where s.sec_id = sectionId) IS NULL
    then
        judge = false; --not found
        return judge;
    else
        judge = true;
        return judge;
    END IF;
END;
$$;

alter function course_found(integer) owner to postgres;

create function course_is_full(sectionid integer) returns boolean
    language plpgsql
as
$$
declare
    judge boolean;
BEGIN
    if (select s.left_capacity
        from section s
        where sec_id = sectionId) = 0
    then
        judge = true;
        return judge;
    else
        judge = false;
        return judge;
    end if;
end
$$;

alter function course_is_full(integer) owner to postgres;

create function course_conflict_found(sectionid integer, studentid integer) returns boolean
    language plpgsql
as
$$
declare
    judge int;
BEGIN
    with temp8 as (
        select u.week_list, u.day_of_week, u.class_begin, u.class_end, u.sem_id
        from (
                 select c.week_list, c.day_of_week, c.class_begin, c.class_end, s2.sem_id
                 from section x
                          join semester s2 on x.semester_id = s2.sem_id
                          join class c on x.sec_id = c.sec_id
                 where x.sec_id = sectionId
             ) u),

         temp9 as (
             select v.week_list, v.day_of_week, v.class_begin, v.class_end, v.sem_id
             from (
                      select c4.week_list, c4.day_of_week, c4.class_begin, c4.class_end, s3.sem_id
                      from learning_info l
                               join section s on l.sec_id = s.sec_id
                               join class c4 on l.sec_id = c4.sec_id
                               join course c5 on s.course_id = c5.course_id
                               join semester s3 on s.semester_id = s3.sem_id
                      where l.sid = studentId
                  ) v)

    select count(*) cnt
    from temp8
             cross join temp9
    where temp8.day_of_week = temp9.day_of_week
      and temp8.sem_id = temp9.sem_id
      and not (temp8.class_end < temp9.class_begin or temp8.class_begin > temp9.class_end)
      and cast(cast(temp8.week_list as bit(32)) & cast(temp9.week_list as bit(32)) as int) != 0
    into judge;

    if judge = 0 then
        return false;
    else
        return true;
    end if;

    --      for inf1 in select * from temp8 loop
--           for inf2 in select * from temp9 loop
--            if((inf1.day_of_week=inf2.day_of_week) and inf1.sem_id=inf2.sem_id
--                 and (inf1.class_begin between inf2.class_begin and inf2.class_end
--                 or inf1.class_end between inf2.class_begin and inf2.class_end)
--                 and cast(cast(inf1.week_list as varbit) & cast(inf2.week_list as varbit) as int) != 0)
--            then
--                judge=true;
--                return judge;
--            end if;
--           end loop;
--     end loop;
end
$$;

alter function course_conflict_found(integer, integer) owner to postgres;

create function already_enrolled(sectionid integer, studentid integer) returns boolean
    language plpgsql
as
$$
declare
    judge bool;
begin
    if not exists(select null
                  from learning_info
                  where sid = studentid
                    and sec_id = sectionid)
    then
        return false;
    end if;

    select grades is null and grading_type is null
    from (select grades, grading_type
          from learning_info
          where sid = studentid
            and sec_id = sectionid) my_grades
    into judge;

    return judge;

end;
$$;

alter function already_enrolled(integer, integer) owner to postgres;

create function already_passed(sectionid integer, studentid integer) returns boolean
    language plpgsql
as
$$
declare
    judge       bool;
    my_courseid varchar;
begin
    --         if not exists(select null from learning_info
--         where sid=studentid and sec_id=sectionid)
--         then return false;
--         end if;

--         select (case when grades is null and grading_type is not null
--                 then grading_type='p'
--             when grading_type is null and grades is not null
--                 then cast(grades as int)>=60
--             else false
--             end)
--         from
--         (select grades, grading_type
--         from learning_info
--         where sid=studentid and sec_id=sectionid)my_grades
--         into judge;

    select course_id
    from section
    where sec_id = sectionid
    into my_courseid;

    select my_courseid in
           (select s.course_id
            from learning_info
                     join section s on learning_info.sec_id = s.sec_id
            where ((grading_type is not null and grading_type = 'p') or (grades is not null and grades >= 60))
              and sid = studentid)
    into judge;


    return judge;

end;
$$;

alter function already_passed(integer, integer) owner to postgres;

create function addenrolledcoursewithgrade(studentid integer, sectionid integer,
                                           grade character varying DEFAULT NULL::character varying) returns void
    language plpgsql
as
$$
BEGIN
    if (grade is not null) then
        if (
               select c.grade_type
               from section s
                        join course c
                             on c.course_id = s.course_id
               where s.sec_id = sectionId
           ) = 0 and (grade = 'p' or grade = 'f') then
            INSERT into learning_info(sid, sec_id, grading_type)
            values (studentId, sectionId, grade);
        elsif (
                  select c.grade_type
                  from section s
                           join course c
                                on c.course_id = s.course_id
                  where s.sec_id = sectionId
              ) = 1 and cast(grade as int) between 0 and 100
        then
            INSERT into learning_info(sid, sec_id, grades)
            values (studentId, sectionId, cast(grade as int));
        else
            raise exception 'invalid grade type' ;
        end if;
    else
        perform enrollcourse(studentid, sectionid);
    end if;
end
$$;

alter function addenrolledcoursewithgrade(integer, integer, varchar) owner to postgres;

create function setenrolledcoursewithgrade(studentid integer, sectionid integer, grade character varying) returns void
    language plpgsql
as
$$
BEGIN
    if (
           select c.grade_type
           from section s
                    join course c
                         on c.course_id = s.course_id
           where s.sec_id = sectionId
       ) = 0 and (grade = 'p' or grade = 'f') then
        update learning_info l
        set grading_type=grade
        where l.sec_id = sectionId
          and l.sid = studentId;
    elsif (
              select c.grade_type
              from section s
                       join course c
                            on c.course_id = s.course_id
              where s.sec_id = sectionId
          ) = 1 and cast(grade as int) between 0 and 100
    then
        update learning_info l
        set grades=cast(grade as int)
        where l.sec_id = sectionId
          and l.sid = studentId;
    else
        raise exception 'invalid grade type' ;
    end if;
end
$$;

alter function setenrolledcoursewithgrade(integer, integer, varchar) owner to postgres;

create function getenrolledcoursesandgrades(studentid integer, semesterid integer DEFAULT NULL::integer)
    returns TABLE
            (
                courseid integer,
                grade    character varying
            )
    language plpgsql
as
$$
begin
    if semesterId is null then
        return query (
            select v.course_name,
                   (
                       case
                           when p.grades is null and p.grading_type is not null then p.grading_type
                           when p.grading_type is null and p.grades is not null then cast(p.grades as varchar)
                           else null
                           end
                       ) grading
            from section t1
                     inner join
                 (select t2.sid, t2.sec_id, t2.grading_type, t2.grades
                  from learning_info t2
                  where t2.sid = studentId
                 ) p on t1.sec_id = p.sid
                     join (
                select c.course_id, c.course_name
                from course c
            ) v on v.course_id = t1.course_id
        );
    else
        return query (
            select v.course_name,
                   (
                       case
                           when p.grades is null and p.grading_type is not null then p.grading_type
                           when p.grading_type is null and p.grades is not null then cast(p.grades as varchar)
                           else null
                           end
                       ) grading
            from section t1
                     inner join
                 (select t2.sid, t2.sec_id, t2.grading_type, t2.grades
                  from learning_info t2
                  where t2.sid = studentId
                 ) p on t1.sec_id = p.sid
                     join (
                select c.course_id, c.course_name
                from course c
            ) v on v.course_id = t1.course_id
                     inner join
                 (select s.sec_id, s.semester_id
                  from section s
                  where s.semester_id = semesterId) q
                 on p.sec_id = q.sec_id
        );
    end if;
end;
$$;

alter function getenrolledcoursesandgrades(integer, integer) owner to postgres;

create function getstudentmajor(studentid integer)
    returns TABLE
            (
                id         integer,
                name       character varying,
                department character varying
            )
    language plpgsql
as
$$
begin
    return query (
        select p.sid, u.first_name || u.last_name, d.dept_name
        from (
                 select *
                 from student_info s
                          join major m on s.major_id = m.major_id
                 where sid = studentId) p
                 join department d on d.dept_id = p.dept_id
                 join users u on u.id = p.sid);
end;
$$;

alter function getstudentmajor(integer) owner to postgres;

create function getcoursebysection(sectionid integer) returns character varying
    language plpgsql
as
$$
begin
    return (
        select s.course_id
        from section s
        where sec_id = sectionId
    );
end;
$$;

alter function getcoursebysection(integer) owner to postgres;

create function full_name(first_name character varying, last_name character varying) returns character varying
    language plpgsql
as
$$
begin
    if (first_name ~ '[a-zA-z]') then
        return first_name || ' ' || last_name;
    else
        return first_name || last_name;
    end if;
end;
$$;

alter function full_name(varchar, varchar) owner to postgres;

create function enrollcourse(studentid integer, sectionid integer) returns void
    language plpgsql
as
$$
begin
    insert into learning_info(sid, sec_id, grades, grading_type)
    values (studentId, sectionId, null, null);
    update section set left_capacity=left_capacity - 1 where semester_id = sectionId;
end;
$$;

alter function enrollcourse(integer, integer) owner to postgres;

create function dropcourse(studentid integer, sectionid integer) returns void
    language plpgsql
as
$$
begin
    delete
    from learning_info
    where sid = studentId
      and sec_id = sectionId;
end;
$$;

alter function dropcourse(integer, integer) owner to postgres;

create function check_pre(c_id character varying, s_id integer) returns boolean
    language plpgsql
as
$$
declare
    exe varchar;
    ans int;
begin
    select format((select pre_pattern from course where course_id = c_id), VARIADIC (select array_agg(state)
                                                                                     from (select pre_list(s_id, c_id) as state) p))
    into exe;
--         select format('((%s | %s) & %s)', VARIADIC array [1,0,1])into exe;
    if exe is null
    then
        return true;
    else
        execute 'select ' || exe INTO ans;
        if ans = 1 then
            return true;
        else
            return false;
        end if;
    end if;
end;
$$;

alter function check_pre(varchar, integer) owner to postgres;

create function pre_list(ssid integer, c_id character varying)
    returns TABLE
            (
                accomplish integer
            )
    language plpgsql
as
$$
begin
    return query
        select (case when learnt is null then 0 else 1 end) judge
        from pre_courses
                 left outer join(
            select s.course_id as learnt
            from student_info
                     join learning_info li on student_info.sid = li.sid
                     join section s on li.sec_id = s.sec_id
            where ((li.grades is not null and li.grades >= 60) or
                   (li.grading_type is not null and li.grading_type = 'p'))
              and li.sid = ssid
        ) s_course
                                on pre_course_id = s_course.learnt
        where course_id = c_id
        order by index;
end;
$$;

alter function pre_list(integer, varchar) owner to postgres;

create function howmany_weeks(in_secid integer) returns integer
    language plpgsql
as
$$
declare
    s_start date;
    s_end   date;
begin
    select sem_begin, sem_end
    from semester
             join section s on semester.sem_id = s.semester_id
    where sec_id = in_secid
    into s_start,s_end;

    return ceil((s_end - s_start) / 7);
end;
$$;

alter function howmany_weeks(integer) owner to postgres;

create function check_conflict(sectionid1 integer, sectionid2 integer) returns boolean
    language plpgsql
as
$$
declare
    judge  int;
    judge2 int;
BEGIN
    with temp8 as (
        select course_id, u.week_list, u.day_of_week, u.class_begin, u.class_end, u.sem_id
        from (
                 select course_id, c.week_list, c.day_of_week, c.class_begin, c.class_end, s2.sem_id
                 from section x
                          join semester s2 on x.semester_id = s2.sem_id
                          join class c on x.sec_id = c.sec_id
                 where x.sec_id = sectionid1
             ) u),

         temp9 as (
             select course_id, v.week_list, v.day_of_week, v.class_begin, v.class_end, v.sem_id
             from (
                      select course_id, c.week_list, c.day_of_week, c.class_begin, c.class_end, s2.sem_id
                      from section x
                               join semester s2 on x.semester_id = s2.sem_id
                               join class c on x.sec_id = c.sec_id
                      where x.sec_id = sectionid2
                  ) v)

    select count(*) cnt
    from temp8
             cross join temp9
    where temp8.day_of_week = temp9.day_of_week
      and temp8.sem_id = temp9.sem_id
      and not (temp8.class_end < temp9.class_begin or temp8.class_begin > temp9.class_end)
      and cast(cast(temp8.week_list as bit(32)) & cast(temp9.week_list as bit(32)) as int) != 0
    into judge;

    --test section conflict

    select count(*) as cnt2
    from (
             select c.course_id
             from section s
                      join course c on c.course_id = s.course_id
             where s.sec_id = sectionid1
             intersect
             select c.course_id
             from section s
                      join course c on c.course_id = s.course_id
             where s.sec_id = sectionid2
         ) same_count
    into judge2;


    if judge = 0 and judge2 = 0 then
        return false;
    else
        return true;
    end if;

end
$$;

alter function check_conflict(integer, integer) owner to postgres;

create function drop_course(studentid integer, sectionid integer) returns void
    language plpgsql
as
$$
begin
    if
        ((select grades from learning_info where sid = studentid and sec_id = sectionid) is null)
    then
        delete
        from learning_info
        where sid = studentId
          and sec_id = sectionId;
    else
        raise exception 'INVALID DROP';
    end if;
end;
$$;

alter function drop_course(integer, integer) owner to postgres;

create function zyl_get_course_table(studentid integer, date1 date)
    returns TABLE
            (
                name         text,
                instructor   character varying,
                instructorid integer,
                classbegin1  integer,
                classend1    integer,
                location1    character varying,
                dyofweek     integer
            )
    language plpgsql
as
$$
begin
    return query (
        select distinct format('%s[%s]', k.course_name, k.sec_name),
                        tea.name          Instructor,
                        tea.instructor_id InstructorId,
                        d.class_begin,
                        d.class_end,
                        d.loc,
                        d.day_of_week
        from (
                 select course_name, s.sec_id, semester_id, sec_name
                 from learning_info l
                          join section s on s.sec_id = l.sec_id
                          join course c on c.course_id = s.course_id
                 where sid = studentid
             ) k
                 join (
            select s.sec_id, c.class_begin, c.class_end, l.loc, c.week_list, c.class_id, c.day_of_week
            from section s
                     join class c on s.sec_id = c.sec_id
                     join location l on l.location_id = c.loc_id
        ) d
                      on k.sec_id = d.sec_id
                 join (
            select distinct ceil(date_part('day', cast(date1 as TIMESTAMP(0)) - cast(s4.sem_begin as TIMESTAMP(0)) +
                                                  interval '1 day') /
                                 7) weeknum
            from semester s4
                     join(
                select *
                from learning_info l
                where l.sid = studentid
            ) b
                         on s4.sem_id in (select semester_id
                                          from learning_info l2
                                                   join section s2 on l2.sec_id = s2.sec_id
                                          where sid = studentid)
        ) sbw on substr(d.week_list, cast(weeknum as integer), 1) =
                 '1'
                 join (
            select full_name(u.first_name, u.last_name) as name, t.class_id, t.instructor_id
            from users u
                     join teaching_info t on u.id = t.instructor_id
        ) tea on tea.class_id = d.class_id
    );
end;
$$;

alter function zyl_get_course_table(integer, date) owner to postgres;

create function getcoursetable(studentid integer, date1 date)
    returns TABLE
            (
                name         text,
                instructor   character varying,
                instructorid integer,
                classbegin1  integer,
                classend1    integer,
                location1    character varying,
                dyofweek     integer
            )
    language plpgsql
as
$$
begin
    return query (
        select distinct format('%s[%s]', k.course_name, k.sec_name),
                        tea.name          Instructor,
                        tea.instructor_id InstructorId,
                        d.class_begin,
                        d.class_end,
                        d.loc,
                        d.day_of_week
        from (
                 select course_name, s.sec_id, semester_id, sec_name
                 from learning_info l
                          join section s on s.sec_id = l.sec_id
                          join course c on c.course_id = s.course_id
                 where sid = studentid
             ) k
                 join (
            select s.sec_id, c.class_begin, c.class_end, l.loc, c.week_list, c.class_id, c.day_of_week
            from section s
                     join class c on s.sec_id = c.sec_id
                     join location l on l.location_id = c.loc_id
        ) d
                      on k.sec_id = d.sec_id
                 join (
            select distinct ceil(date_part('day', cast(date1 as TIMESTAMP(0)) - cast(s4.sem_begin as TIMESTAMP(0))) /
                                 7) weeknum
            from semester s4
                     join(
                select *
                from learning_info l
                where l.sid = studentid
            ) b
                         on s4.sem_id in (select semester_id
                                          from learning_info l2
                                                   join section s2 on l2.sec_id = s2.sec_id
                                          where sid = studentid)
        ) sbw on substr(d.week_list, case cast(weeknum as integer) when 0 then 1 else cast(weeknum as integer) end, 1) =
                 '1'
                 join (
            select full_name(u.first_name, u.last_name) as name, t.class_id, t.instructor_id
            from users u
                     join teaching_info t on u.id = t.instructor_id
        ) tea on tea.class_id = d.class_id
    );
end;
$$;

alter function getcoursetable(integer, date) owner to postgres;

create function already_passed_course(sectionid integer, studentid integer) returns boolean
    language plpgsql
as
$$
declare
    judge bool;
begin
    if not exists(select null
                  from learning_info
                  where sid = studentid
                    and sec_id = sectionid)
    then
        return false;
    end if;

    select (case
                when grades is null and grading_type is not null
                    then grading_type = 'p'
                when grading_type is null and grades is not null
                    then cast(grades as int) >= 60
                else false
        end)
    from (select s2.sec_id
          from section s2
                   join
               (select c.course_id
                from course c
                         join section s
                              on c.course_id = s.course_id
                                  and s.sec_id = sectionid) m
               on s2.course_id = m.course_id
         ) mg
             join learning_info l2 on l2.sec_id = mg.sec_id
    where sid = studentid
    into judge;
    return judge;
end;
$$;

alter function already_passed_course(integer, integer) owner to postgres;

create function old_super_super_search_course(st_id integer, sm_id integer, scourse_id character varying,
                                              scourse_name character varying, sinstructor_name character varying,
                                              day_of_w integer, sclass_time integer, sloc character varying,
                                              scourse_type integer, ignore_cfl boolean, ignore_full boolean,
                                              ignore_pass boolean, ignore_missing_pre boolean, page_size integer,
                                              page_index integer)
    returns TABLE
            (
                course_id        character varying,
                course_name      character varying,
                credit           integer,
                course_hour      integer,
                grade_type       integer,
                sec_id           integer,
                sec_name         character varying,
                tot_capacity     integer,
                left_capacity    integer,
                class_id         integer,
                class_begin      integer,
                class_end        integer,
                week_list        character varying,
                day_of_week      integer,
                instructor_id    integer,
                first_name       character varying,
                last_name        character varying,
                loc              character varying,
                conflict_courses text[]
            )
    language plpgsql
as
$$
declare
    wisdom varchar;
    lofty  varchar;
    hiraki varchar;
begin
    wisdom := 'select c.course_id, course_name, credit, course_hour, grade_type, s.sec_id, sec_name, tot_capacity, left_capacity, mc.type
from course c
join section s on c.course_id = s.course_id
join class c2 on s.sec_id = c2.sec_id
join location l on c2.loc_id = l.location_id
join teaching_info ti on c2.class_id = ti.class_id
join users u on ti.instructor_id = u.id
left outer join major_course mc on c.course_id = mc.course_id
where semester_id=' || sm_id;
    --phase 2
    if scourse_id is not null
    then
        wisdom := wisdom || ' and c.course_id~''' || scourse_id || '''';
    end if;

    --phase 3
    if scourse_name is not null
    then
        wisdom := wisdom || ' and format(''%s[%s]'', c.course_name, s.sec_name)~E''' || scourse_name || '''';
    end if;

    --phase 4
    if sinstructor_name is not null
    then
        wisdom := wisdom || ' and full_name(u.first_name, u.last_name)~''' || sinstructor_name || '''';
    end if;

    -- phase 5
    if day_of_w is not null
    then
        wisdom := wisdom || ' and day_of_week=' || day_of_w;
    end if;

    --phase 6
    if sclass_time is not null
    then
        wisdom := wisdom || ' and ' || sclass_time || ' between c2.class_begin and c2.class_end';
    end if;

    --phase 7
    if sloc is not null
    then
        wisdom := wisdom || ' and (select bool_or(pp.cc)
from(
select l.loc ~ p.u as cc
from (select unnest(array[' || sloc || ']) as u)p )pp)';
    end if;

    --phase 8
    if scourse_type is not null
    then
        case when scourse_type = 1
            then wisdom := wisdom || ' and mc.type=1';
            when scourse_type = 2
                then wisdom := wisdom || ' and mc.type=0';
            when scourse_type = 3
                then wisdom := wisdom ||
                               ' and (mc.type is not null and mc.type != (select major_id from student_info where sid=' ||
                               st_id || ' ))';
            when scourse_type = 4
                then wisdom := wisdom || ' and mc.type is null';
            else wisdom := wisdom;
            end case;
    end if;

    --phase 9
    if ignore_full
    then
        wisdom := wisdom || ' and s.left_capacity>=0';
    end if;

    --phase 10
    if ignore_cfl
    then
        wisdom := wisdom || ' and ((not already_passed(s.sec_id,' || st_id || ') and not already_enrolled(s.sec_id,' ||
                  st_id || '))
            and not course_conflict_found(s.sec_id,' || st_id || ')) ';
    end if;

    --phase 11
    if ignore_pass
    then
        wisdom := wisdom || ' and not already_passed(s.sec_id, ' || st_id || ')';
    end if;

    --phase 12
    if ignore_missing_pre
    then
        wisdom := wisdom || ' and check_pre(c.course_id,' || st_id || ')';
    end if;


    --         --final phase
--         lofty:='select course_id, course_name, credit, course_hour, grade_type, sec_id, sec_name, tot_capacity, left_capacity,
--        class_id, class_begin, class_end, week_list, day_of_week,
--                instructor_id,first_name, last_name, loc,
--
--                (select array_agg( format(''%s[%s]'', ppp.course_name, ppp.sec_name))
--                 from (select c.course_name, s.sec_name
--                     from(select li.sec_id, li.sid from
--                     learning_info li )lii
--                     join section s on lii.sec_id = s.sec_id
--                     join course c on c.course_id = s.course_id
--                     where lii.sid='|| st_id ||' and (check_conflict(s.sec_id, tp.sec_id) or c.course_name=tp.course_name)
--                order by c.course_name, s.sec_name)ppp) as conflict_courses
--     from ('||wisdom||')tp'||' order by tp.course_id, tp.course_name, tp.sec_name';
    --final phase
    lofty := 'select course_id, course_name, credit, course_hour, grade_type, tp.sec_id, sec_name, tot_capacity, left_capacity,
       cc.class_id, class_begin, class_end, week_list, day_of_week,
               instructor_id,first_name, last_name, loc,
               (select array_agg( format(''%s[%s]'', ppp.course_name, ppp.sec_name))
                from (select c.course_name, s.sec_name
                    from(select li.sec_id, li.sid from
                    learning_info li )lii
                    join section s on lii.sec_id = s.sec_id
                    join course c on c.course_id = s.course_id
                    where lii.sid=' || st_id || ' and (check_conflict(s.sec_id, tp.sec_id) or c.course_name=tp.course_name)
               order by c.course_name, s.sec_name)ppp) as conflict_courses
    from (' || wisdom || ')tp' || ' ' ||
             'join class cc on cc.sec_id = tp.sec_id' ||
             '
join location l on cc.loc_id = l.location_id
join teaching_info ti on cc.class_id = ti.class_id
join users u on ti.instructor_id = u.id' ||
             ' order by tp.course_id, tp.course_name, tp.sec_name';

    hiraki := 'select distinct course_id, course_name, credit, course_hour, grade_type, sec_id, sec_name, tot_capacity, left_capacity,
       class_id, class_begin, class_end, week_list, day_of_week,
               instructor_id,first_name, last_name, loc, conflict_courses' ||
              ' from (' || lofty || ')mw';

    return query execute hiraki;

end;
$$;

alter function old_super_super_search_course(integer, integer, varchar, varchar, varchar, integer, integer, varchar, integer, boolean, boolean, boolean, boolean, integer, integer) owner to postgres;

create function super_super_search_course(st_id integer, sm_id integer, scourse_id character varying,
                                          scourse_name character varying, sinstructor_name character varying,
                                          day_of_w integer, sclass_time integer, sloc character varying,
                                          scourse_type integer, ignore_cfl boolean, ignore_full boolean,
                                          ignore_pass boolean, ignore_missing_pre boolean, page_size integer,
                                          page_index integer)
    returns TABLE
            (
                course_id        character varying,
                course_name      character varying,
                credit           integer,
                course_hour      integer,
                grade_type       integer,
                sec_id           integer,
                sec_name         character varying,
                tot_capacity     integer,
                left_capacity    integer,
                class_id         integer,
                class_begin      integer,
                class_end        integer,
                week_list        character varying,
                day_of_week      integer,
                instructor_id    integer,
                first_name       character varying,
                last_name        character varying,
                loc              character varying,
                conflict_courses text[]
            )
    language plpgsql
as
$$
declare
    wisdom varchar;
    lofty  varchar;
begin
    wisdom := 'select c.course_id, course_name, credit, course_hour, grade_type, s.sec_id, sec_name, tot_capacity, left_capacity, c2.class_id, u.first_name, u.last_name,
c2.class_begin, c2.class_end, mc.type, ti.instructor_id, week_list, day_of_week, loc
from course c
join section s on c.course_id = s.course_id
join class c2 on s.sec_id = c2.sec_id
join location l on c2.loc_id = l.location_id
join teaching_info ti on c2.class_id = ti.class_id
join users u on ti.instructor_id = u.id
left outer join major_course mc on c.course_id = mc.course_id
where semester_id=' || sm_id;
    --phase 2
    if scourse_id is not null
    then
        wisdom := 'select *
                from (' || wisdom || ') p1
                where p1.course_id ~ ''' || scourse_id || '''';
    end if;

    --phase 3
    if scourse_name is not null
    then
        wisdom := 'select *
                from (' || wisdom || ') p2
                where format(''%s[%s]'', p2.course_name, p2.sec_name)~E''' || scourse_name || '''';
    end if;

    --phase 4
    if sinstructor_name is not null
    then
        wisdom := 'select *
                from (' || wisdom || ') p3
                where full_name(p3.first_name, p3.last_name)~''' || sinstructor_name || '''';
    end if;

    -- phase 5
    if day_of_w is not null
    then
        wisdom := 'select *
                from (' || wisdom || ') p4
                where p4.day_of_week=' || day_of_w;
    end if;

    --phase 6
    if sclass_time is not null
    then
        wisdom := 'select *
                from (' || wisdom || ') p5
                where ' || sclass_time || ' between p5.class_begin and p5.class_end';
    end if;

    --phase 7
    if sloc is not null
    then
        wisdom := 'select *
                from (' || wisdom || ') p6
                where (select bool_or(pp.cc)
from(
select p6.loc ~ p.u as cc
from (select unnest(array[' || sloc || ']) as u)p )pp)';
    end if;

    --phase 8
    if scourse_type is not null
    then
        case when scourse_type = 1
            then wisdom := 'select *
                from (' || wisdom || ') p7
                where p7.type=1';
            when scourse_type = 2
                then wisdom := 'select *
                from (' || wisdom || ') p7
                where p7.type=0';
            when scourse_type = 3
                then wisdom := 'select *
                from (' || wisdom || ') p7
                where (p7.type is not null and p7.type != (select major_id from student_info where sid=' || st_id ||
                               ' ))';
            when scourse_type = 4
                then wisdom := 'select *
                from (' || wisdom || ') p7
                where p7.type is null';
            else wisdom := wisdom;
            end case;
    end if;

    --phase 9
    if ignore_full
    then
        wisdom := 'select *
                from (' || wisdom || ') p8
                where p8.left_capacity>=0';
    end if;

    --phase 10
    if ignore_cfl
    then
        wisdom := 'select *
                from (' || wisdom || ') p9
                where ((not already_passed(p9.sec_id,' || st_id || ') and not already_enrolled(p9.sec_id,' || st_id || '))
            and not course_conflict_found(p9.sec_id,' || st_id || ')) ';
    end if;
    --phase 11
    if ignore_pass
    then
        wisdom := 'select *
                from (' || wisdom || ') p9
                where not already_passed(p9.sec_id, ' || st_id || ')';
    end if;

    --phase 12
    if ignore_missing_pre
    then
        wisdom := 'select *
                from (' || wisdom || ') p10
                where check_pre(p10.course_id,' || st_id || ')';
    end if;


    --final phase
--         lofty:='select course_id, course_name, credit, course_hour, grade_type, sec_id, sec_name, tot_capacity, left_capacity,
--        class_id, class_begin, class_end, week_list, day_of_week,
--                instructor_id,first_name, last_name, loc,
--
--                (select array_agg( format(''%s[%s]'', ppp.course_name, ppp.sec_name))
--                 from (select c.course_name, s.sec_name
--                     from
--                     learning_info li
--                     join section s on li.sec_id = s.sec_id
--                     join course c on c.course_id = s.course_id
--                     where sid='|| st_id ||' and check_conflict(s.sec_id, tp.sec_id)
--                order by c.course_name, s.sec_name)ppp) as conflict_courses
--     from ('||wisdom||')tp'||' order by tp.course_id, tp.course_name, tp.sec_name';


    lofty = 'select cc.course_id, cc.course_name, cc.credit, course_hour, grade_type, s.sec_id, s.sec_name, tot_capacity, left_capacity,
       class.class_id, class_begin, class_end, week_list, day_of_week,
               ti.instructor_id,first_name, last_name, loc,

               (select array_agg( format(''%s[%s]'', ppp.course_name, ppp.sec_name))
                from (select c.course_name, s.sec_name
                    from
                    learning_info li
                    join section s on li.sec_id = s.sec_id
                    join course c on c.course_id = s.course_id
                    where sid=' || st_id || ' and check_conflict(s.sec_id, tp.sec_id)
               order by c.course_name, s.sec_name)ppp) as conflict_courses
       from (select distinct ww.sec_id from (' || wisdom || ')ww)tp
        join section s on s.sec_id=tp.sec_id
        join class on s.sec_id = class.sec_id
        join course cc on cc.course_id=s.course_id
        join location l on class.loc_id = l.location_id
        join teaching_info ti on class.class_id = ti.class_id' ||
            ' join users u on ti.instructor_id=u.id
      order by cc.course_id, cc.course_name, s.sec_name';


    return query execute lofty;

end;
$$;

alter function super_super_search_course(integer, integer, varchar, varchar, varchar, integer, integer, varchar, integer, boolean, boolean, boolean, boolean, integer, integer) owner to postgres;

create function zyl_course_conflict_found(sectionid integer, studentid integer) returns boolean
    language plpgsql
as
$$
declare
    judge  int;
    judge2 int;
BEGIN
    --test time conflict
    with temp8 as (
        select u.week_list, u.day_of_week, u.class_begin, u.class_end, u.sem_id
        from (
                 select c.week_list, c.day_of_week, c.class_begin, c.class_end, s2.sem_id
                 from section x
                          join semester s2 on x.semester_id = s2.sem_id
                          join class c on x.sec_id = c.sec_id
                 where x.sec_id = sectionId
             ) u),

         temp9 as (
             select v.week_list, v.day_of_week, v.class_begin, v.class_end, v.sem_id
             from (
                      select c4.week_list, c4.day_of_week, c4.class_begin, c4.class_end, s3.sem_id
                      from learning_info l
                               join section s on l.sec_id = s.sec_id
                               join class c4 on l.sec_id = c4.sec_id
                               join course c5 on s.course_id = c5.course_id
                               join semester s3 on s.semester_id = s3.sem_id
                      where l.sid = studentId
                  ) v)

    select count(*) cnt
    from temp8
             cross join temp9
    where temp8.day_of_week = temp9.day_of_week
      and temp8.sem_id = temp9.sem_id
      and not (temp8.class_end < temp9.class_begin or temp8.class_begin > temp9.class_end)
      and cast(cast(temp8.week_list as bit(32)) & cast(temp9.week_list as bit(32)) as int) != 0
    into judge;

    --test section conflict

    select count(*) as cnt2
    from (
             select c.course_id
             from section s
                      join course c on c.course_id = s.course_id
             where s.sec_id = sectionid
             intersect
             select distinct c.course_id
             from learning_info lf
                      join section s on s.sec_id = lf.sec_id
                      join course c on c.course_id = s.course_id
                      join student_info si on lf.sid = si.sid
                      join semester s2 on s2.sem_id = s.semester_id
             where si.sid = studentid
               and s2.sem_id = (select seccc.semester_id from section seccc where seccc.sec_id = sectionid)
         ) same_count
    into judge2;


    if judge = 0 and judge2 = 0 then
        return false;
    else
        return true;
    end if;
end
$$;

alter function zyl_course_conflict_found(integer, integer) owner to postgres;

create function zyl_already_enrolled(sectionid integer, studentid integer) returns boolean
    language plpgsql
as
$$
declare
    judge bool;
begin
    if exists(select null
              from learning_info
              where sid = studentid
                and sec_id = sectionid)
    then
        return true;
    else
        return false;
    end if;

    --     select grades is null and grading_type is null
--     from (select grades, grading_type
--           from learning_info
--           where sid = studentid
--             and sec_id = sectionid) my_grades
--     into judge;
--
--     return judge;

end;
$$;

alter function zyl_already_enrolled(integer, integer) owner to postgres;

create function zyl_already_passed_course(sectionid integer, studentid integer) returns boolean
    language plpgsql
as
$$
declare
    judge bool;
begin
    --     if not exists(select null
--                   from learning_info
--                   where sid = studentid
--                     and sec_id = sectionid)
--     then
--         return false;
--     end if;

    select (case
                when grades is null and grading_type is not null
                    then grading_type = 'p'
                when grading_type is null and grades is not null
                    then cast(grades as int) >= 60
                else false
        end)
    from (select grades, grading_type
          from section s
                   join course c on c.course_id = s.course_id
                   join section s2 on c.course_id = s2.course_id and s2.sec_id = sectionid
                   join learning_info li on s.sec_id = li.sec_id and li.sid = studentid
         ) student_info
    into judge;
    return judge;
end;
$$;

alter function zyl_already_passed_course(integer, integer) owner to postgres;

create function super_search_course(st_id integer, sm_id integer, scourse_id character varying,
                                    scourse_name character varying, sinstructor_name character varying,
                                    day_of_w integer, sclass_time integer, sloc character varying, scourse_type integer,
                                    ignore_cfl boolean, ignore_full boolean, ignore_pass boolean,
                                    ignore_missing_pre boolean, page_size integer, page_index integer)
    returns TABLE
            (
                course_id        character varying,
                course_name      character varying,
                credit           integer,
                course_hour      integer,
                grade_type       integer,
                sec_id           integer,
                sec_name         character varying,
                tot_capacity     integer,
                left_capacity    integer,
                class_id         integer,
                class_begin      integer,
                class_end        integer,
                week_list        character varying,
                day_of_week      integer,
                instructor_id    integer,
                first_name       character varying,
                last_name        character varying,
                loc              character varying,
                conflict_courses text[]
            )
    language plpgsql
as
$$
declare
    wisdom      varchar;
    lofty       varchar;
    my_major_id int;
begin
    select major_id from student_info where sid = st_id into my_major_id;


    wisdom := 'select c.course_id, course_name, credit, course_hour, grade_type, s.sec_id, sec_name, tot_capacity, left_capacity, c2.class_id, u.first_name, u.last_name,

c2.class_begin, c2.class_end, mc.type, ti.instructor_id, week_list, day_of_week, loc
from course c
join section s on c.course_id = s.course_id
join class c2 on s.sec_id = c2.sec_id
join location l on c2.loc_id = l.location_id
join teaching_info ti on c2.class_id = ti.class_id
join users u on ti.instructor_id = u.id
left outer join major_course mc on c.course_id = mc.course_id
where semester_id=' || sm_id;
    --phase 2
    if scourse_id is not null
    then
        wisdom := wisdom || ' and c.course_id~''' || scourse_id || '''';
    end if;

    --phase 3
    if scourse_name is not null
    then
        wisdom := wisdom || ' and format(''%s[%s]'', c.course_name, s.sec_name)~E''' || scourse_name || '''';
    end if;

    --phase 4
    if sinstructor_name is not null
    then
        wisdom := wisdom || ' and (full_name(u.first_name, u.last_name)~''' || sinstructor_name ||
                  ''' or u.first_name||u.last_name~''' || sinstructor_name || ''')';
    end if;

    -- phase 5
    if day_of_w is not null
    then
        wisdom := wisdom || ' and day_of_week=' || day_of_w;
    end if;

    --phase 6
    if sclass_time is not null
    then
        wisdom := wisdom || ' and ' || sclass_time || ' between c2.class_begin and c2.class_end';
    end if;

    --phase 7
    if sloc is not null
    then
        wisdom := wisdom || ' and (select bool_or(pp.cc)
from(
select l.loc ~ p.u as cc
from (select unnest(array[' || sloc || ']) as u)p )pp)';
    end if;

    --phase 8
    if scourse_type is not null
    then
        case when scourse_type = 1
            then wisdom := wisdom || ' and mc.type=1 and mc.major_id=' || my_major_id;
            when scourse_type = 2
                then wisdom := wisdom || ' and mc.type=0 and mc.major_id=' || my_major_id;
            when scourse_type = 3
                then wisdom := wisdom || ' and (mc.type is not null and mc.major_id != ' || my_major_id || ')';
            when scourse_type = 4
                then wisdom := wisdom || ' and mc.type is null';
            else wisdom := wisdom;
            end case;
    end if;

    --phase 9
    if ignore_full
    then
        wisdom := wisdom || ' and s.left_capacity>=0';
    end if;

    --phase 10
    if ignore_cfl
    then
        wisdom := wisdom || ' and not zyl_course_conflict_found(s.sec_id, ' || st_id || ')';
    end if;

    --phase 11
    if ignore_pass
    then
        wisdom := wisdom || ' and not already_passed(s.sec_id, ' || st_id || ')';
    end if;

    --phase 12
    if ignore_missing_pre
    then
        wisdom := wisdom || ' and check_pre(c.course_id,' || st_id || ')';
    end if;


    --         --final phase
--         lofty:='select course_id, course_name, credit, course_hour, grade_type, sec_id, sec_name, tot_capacity, left_capacity,
--        class_id, class_begin, class_end, week_list, day_of_week,
--                instructor_id,first_name, last_name, loc,
--
--                (select array_agg( format(''%s[%s]'', ppp.course_name, ppp.sec_name))
--                 from (select c.course_name, s.sec_name
--                     from
--                     learning_info li
--                     join section s on li.sec_id = s.sec_id
--                     join course c on c.course_id = s.course_id
--                     where sid='|| st_id ||' and check_conflict(s.sec_id, tp.sec_id)
--                order by c.course_name, s.sec_name)ppp) as conflict_courses
--     from ('||wisdom||')tp'||' order by tp.course_id, tp.course_name, tp.sec_name';


    lofty = 'select cc.course_id, cc.course_name, cc.credit, course_hour, grade_type, s.sec_id, s.sec_name, tot_capacity, left_capacity,
       class.class_id, class_begin, class_end, week_list, day_of_week,
               ti.instructor_id,first_name, last_name, loc,

               (select array_agg( format(''%s[%s]'', ppp.course_name, ppp.sec_name))
                from (select c.course_name, s.sec_name, s.semester_id
                    from
                    learning_info li
                    join section s on li.sec_id = s.sec_id
                    join course c on c.course_id = s.course_id
                    where sid=' || st_id || ' and check_conflict(s.sec_id, tp.sec_id) and s.semester_id=' || sm_id || '
               order by c.course_name, s.sec_name)ppp) as conflict_courses
       from (select distinct ww.sec_id from (' || wisdom || ')ww)tp
        join section s on s.sec_id=tp.sec_id
        join class on s.sec_id = class.sec_id
        join course cc on cc.course_id=s.course_id
        join location l on class.loc_id = l.location_id
        join teaching_info ti on class.class_id = ti.class_id' ||
            ' join users u on ti.instructor_id=u.id
      order by cc.course_id, cc.course_name, s.sec_name';
    return query execute lofty;

end;
$$;

alter function super_search_course(integer, integer, varchar, varchar, varchar, integer, integer, varchar, integer, boolean, boolean, boolean, boolean, integer, integer) owner to postgres;

create function zyl_super_already_passed_course(sectionid integer, studentid integer) returns boolean
    language plpgsql
as
$$
declare
    judge bool;
begin

    select (case
                when grades is null and grading_type is not null
                    then grading_type = 'p'
                when grading_type is null and grades is not null
                    then cast(grades as int) >= 60
                else false
        end)
    from (select grades, grading_type
          from section s
                   join course c on c.course_id = s.course_id
                   join section s2 on c.course_id = s2.course_id and s2.sec_id in (select sec_id
                                                                                   from section
                                                                                   where course_id in (
                                                                                       select course_id
                                                                                       from section
                                                                                       where sec_id = sectionid))-- s2.sec_id=sectionid
                   join learning_info li on s.sec_id = li.sec_id and li.sid = studentid
         ) student_info
    into judge;
    return judge;
end;
$$;

alter function zyl_super_already_passed_course(integer, integer) owner to postgres;

create function zyl_drop_course(studentid integer, sectionid integer) returns boolean
    language plpgsql
as
$$
begin
    if
        ((select grades from learning_info where sid = studentid and sec_id = sectionid) is null
            and
         (select grading_type from learning_info where sid = studentid and sec_id = sectionid) is null
            )
    then
        delete
        from learning_info
        where sid = studentId
          and sec_id = sectionId;
        return true;
    else
        return false;
--         raise exception 'INVALID DROP';
    end if;
end;
$$;

alter function zyl_drop_course(integer, integer) owner to postgres;

