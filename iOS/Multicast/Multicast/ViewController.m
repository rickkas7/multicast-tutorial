//
//  ViewController.m
//  Multicast
//
//  Created by Rick on 2/6/16.
//  Copyright © 2016 Rick Kaseguma. All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];

    self.items = [[NSMutableArray alloc] init];
    [self setupSocket];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}


- (void)setupSocket
{
    // http://stackoverflow.com/questions/13459116/how-to-receive-multicast-udp
    self.udpSocket = [[GCDAsyncUdpSocket alloc] initWithDelegate:self delegateQueue:dispatch_get_main_queue()];
    NSError *error = nil;
    if (![self.udpSocket bindToPort:7234 error:&error])
    {
        NSLog(@"Error binding to port: %@", error);
        return;
    }
    if(![self.udpSocket joinMulticastGroup:@"239.1.1.234" error:&error]){
        NSLog(@"Error connecting to multicast group: %@", error);
        return;
    }
    if (![self.udpSocket beginReceiving:&error])
    {
        NSLog(@"Error receiving: %@", error);
        return;
    }
    NSLog(@"Socket Ready");
}

// GCDAsyncUdpSocketDelegate
- (void)udpSocket:(GCDAsyncUdpSocket *)sock didReceiveData:(NSData *)data
    fromAddress:(NSData *)address withFilterContext:(id)filterContext
{
    const unsigned char *bytes = [data bytes];
    
    int value = (bytes[0] << 8) | bytes[1];

    NSString *str = [[NSString alloc] initWithFormat:@"%d", value];
    [self.items addObject:str];
    [self.tableView reloadData];

    
    NSLog(@"got data %d", value);
}

// UITableViewDelegate
- (NSInteger)tableView:(UITableView *)tableView
 numberOfRowsInSection:(NSInteger)section
{
    return [self.items count];
}

// UITableViewDataSource
- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *SimpleTableIdentifier = @"SimpleTableIdentifier";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:
                             SimpleTableIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc]
                initWithStyle:UITableViewCellStyleDefault
                reuseIdentifier:SimpleTableIdentifier];
    }
    cell.textLabel.text = [self.items objectAtIndex:indexPath.row];
    return cell;
}

@end
