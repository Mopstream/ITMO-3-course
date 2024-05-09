`timescale 1ns / 1ps

module multer(
    input clk,
    input rst,

    input [7:0] a,
    input [7:0] b,
    input start,

    output busy,
    output reg [15:0] y
);

localparam IDLE = 1'b0;
localparam WORK = 1'b1;

reg [3:0] ctr;
wire [3:0] end_step;
wire [7:0] part_sum;
wire [15:0] shifted_part_sum;
reg [7:0] _a, _b;
reg [15:0] part_res;
reg state;

assign part_sum = a & {8{b[ctr]}};
assign shifted_part_sum = part_sum << ctr;
assign end_step = (ctr == 4'h8);
assign busy = state;

always @(posedge clk)
    if (rst) begin
        ctr <= 0;
        part_res <= 0;
        y <= 0;

        state <= IDLE;
    end else begin
        case (state)
            IDLE :
                if (start) begin
                    state <= WORK;

                    _a <= a;
                    _b <= b;
                    ctr <= 0;
                    part_res <= 0;
                end
            WORK:
                begin
                    if (end_step) begin
                        y <= part_res;
                        state <= IDLE;
                    end

                    part_res <= part_res + shifted_part_sum;
                    ctr <= ctr + 1;
                end
        endcase
    end

endmodule