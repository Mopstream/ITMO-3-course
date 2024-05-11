`timescale 1ns / 1ps

module adder_tb();

reg clk;
parameter PERIOD = 5;
initial begin
    clk = 1;
    forever
        #PERIOD
        clk = ~clk;
end


reg [8:0] i, j;
reg [8:0] test_out;
wire is_end_i, is_end_j;

reg [7:0] in_a, in_b;
reg start, in_rst;

wire [8:0] out;
wire out_busy;

adder summ(
    .a(in_a),
    .b(in_b),
    .res(out)
);

assign is_end_i = (i == 256);
assign is_end_j = (j == 256);

localparam PREPARE = 3'd0;
localparam CYCLE_I = 3'd1;
localparam CYCLE_J = 3'd2;
localparam NEXT_J = 3'd3;
localparam BODY = 3'd4;
localparam BODY_1 = 3'd5;

reg [3:0] state = PREPARE;

always @(posedge clk) begin
    case (state)
        PREPARE:
            begin
                i <= 8'd0;
                j <= 8'd0;
                in_rst <= 1;
                state <= CYCLE_J;
            end
        CYCLE_I:
            begin
                if (is_end_i) begin
                    $finish;
                end else begin
                    j <= 0;
                    state <= CYCLE_J;
                end
            end
        CYCLE_J:
            begin
                if (is_end_j) begin
                    i <= i + 1;
                    state <= CYCLE_I;
                end else begin
                    state <= BODY;
                end
            end
        NEXT_J:
            begin
                j <= j + 1;
                state <= CYCLE_J;
            end
        BODY:
            begin
                in_a <= i;
                in_b <= j;
                test_out <= i + j;
                state <= BODY_1;
            end
        BODY_1:
            begin
                if (out == test_out) begin
                    $display("Correct! a_bi = %0d, b_bi = %0d, y_bo = %0d, expected = %0d", in_a, in_b, out, test_out);
                end else begin
                    $display("Incorrect! a_bi = %0d, b_bi = %0d, y_bo = %0d, expected = %0d", in_a, in_b, out, test_out);
                end
                state <= NEXT_J;
            end
    endcase
end

endmodule