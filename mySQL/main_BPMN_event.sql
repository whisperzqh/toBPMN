INSERT INTO BPMN_event (id, e_id, name, description, process_id) VALUES (1, 'E00', 'POaccepted', 'Order approved', 'T02');
INSERT INTO BPMN_event (id, e_id, name, description, process_id) VALUES (2, 'E01', 'approved1', 'The payment document submitted by the platform to Party A is approved', 'T04');
INSERT INTO BPMN_event (id, e_id, name, description, process_id) VALUES (3, 'E02', 'pay1', 'Party A completes the payment to the platform', 'T06、T07');
INSERT INTO BPMN_event (id, e_id, name, description, process_id) VALUES (4, 'E03', 'pay3pay3', 'Completion of loan transfer from the platform to Party B', 'T13');
INSERT INTO BPMN_event (id, e_id, name, description, process_id) VALUES (5, 'E04', 'ship_goods', 'Delivery by Party B', 'T09');
INSERT INTO BPMN_event (id, e_id, name, description, process_id) VALUES (6, 'E05', 'receive_goods', 'Party A receives the goods', 'T09');
INSERT INTO BPMN_event (id, e_id, name, description, process_id) VALUES (7, 'E06', 'unreceive_goods', 'Party A has not received the goods', 'T09');