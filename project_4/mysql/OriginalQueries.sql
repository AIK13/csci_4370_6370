
-- Set variables for queries. Can be changed to different values.
SET @v1 = 75;
SET @v2 = 100;
SET @v3 = 900;
SET @v4 = 'crsCode44887';
SET @v5 = 'name334241';
SET @v6 = 'deptId999245';
SET @v7 = 'deptId995293';
SET @v8 = 'deptId999245';

-- Query 1: List the name of the student with id equal to v1 (id).
SELECT name 
FROM Student 
WHERE id = @v1;


-- Query 2: List the names of students with id in the range of v2 (id) to v3 (inclusive).
SELECT name 
FROM Student 
WHERE id >= @v2 AND id <= @v3;


-- Query 3: List the names of students who have taken course v4 (crsCode).
SELECT name 
FROM Student 
WHERE id IN (SELECT studId FROM Transcript WHERE crsCode = @v4);


-- Query 4: List the names of students who have taken a course taught by professor v5 (name).
SELECT name 
FROM Student, 
	(SELECT studId FROM Transcript,
		(SELECT crsCode, semester 
		FROM Professor
			JOIN Teaching
		WHERE Professor.name = @v5 AND Professor.id = Teaching.profID) as alias
	WHERE Transcript.crsCode = alias.crsCode AND Transcript.semester = alias.semester) as alias1
WHERE Student.id = alias1.studID;
    
    
-- Query 5: List the names of students who have taken a course from department v6 (deptId), but not v7.
SELECT name 
FROM Student,
    (SELECT studID 
    FROM Transcript, Course 
    WHERE deptId = @v6 AND Transcript.crsCode = Course.crsCode
    AND studID NOT IN
    (SELECT studId 
    FROM Transcript, Course 
    WHERE deptID = @v7 AND Transcript.crsCode = Course.crsCode)) as alias
WHERE Student.id = alias.studId;


-- Query 6: List the names of students who have taken all courses offered by department v8 (deptId).
SELECT name 
FROM Student,
	(SELECT studId
	FROM Transcript
	WHERE crsCode IN
	(SELECT crsCode 
	FROM Course 
	WHERE deptId = @v8 AND crsCode IN (SELECT crsCode FROM Teaching))
	GROUP BY studId
	HAVING COUNT(*) = 
	  (SELECT COUNT(*) 
	  FROM Course 
    WHERE deptId = @v8 AND crsCode IN (SELECT crsCode FROM Teaching))) as alias
WHERE id = alias.studId;
